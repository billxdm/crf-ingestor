package com.ecfr.service;

import com.ecfr.dto.ecfrxml.*;
import com.ecfr.model.Paragraph;
import com.ecfr.repository.EcfrRepository;
import com.ecfr.service.EcfrValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import jakarta.annotation.PreDestroy;
import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;

@Service
public class EcfrIngestionService {
    private static final Logger log = LoggerFactory.getLogger(EcfrIngestionService.class);
    
    private final RestTemplate restTemplate;
    private final EcfrXmlParser xmlParser;
    private final EcfrRepository repository;
    private final EcfrValidationService validationService;
    private final ExecutorService executorService;
    private final MongoTemplate mongoTemplate;
    
    @Value("${ecfr.api.base-url:https://www.ecfr.gov/api/versioner/v1}")
    private String baseUrl;
    
    @Value("${ecfr.api.batch-size:50}")
    private int batchSize;
    
    @Value("${ecfr.api.max-retries:3}")
    private int maxRetries;
    
    @Value("${ecfr.api.bulk-size:1000}")
    private int bulkSize;

    @Autowired
    private EcfrDocumentRepository documentRepository;

    @Autowired
    private BatchIngestionService batchIngestionService;
    
    public EcfrIngestionService(RestTemplate restTemplate, 
                               EcfrXmlParser xmlParser,
                               EcfrRepository repository,
                               EcfrValidationService validationService,
                               MongoTemplate mongoTemplate) {
        this.restTemplate = restTemplate;
        this.xmlParser = xmlParser;
        this.repository = repository;
        this.validationService = validationService;
        this.mongoTemplate = mongoTemplate;
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );
    }
    
    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public EcfrDTO ingestTitle(String titleNumber, LocalDate effectiveDate) {
        String url = buildUrl(titleNumber, effectiveDate);
        log.info("Fetching eCFR data from URL: {}", url);
        
        try {
            EcfrDTO ecfr = xmlParser.parseEcfrXml(url);
            log.info("Successfully parsed eCFR data for title {}", titleNumber);
            
            if (!validationService.validateEcfrData(ecfr)) {
                throw new RuntimeException("Invalid eCFR data for title " + titleNumber);
            }
            
            return repository.save(ecfr);
        } catch (Exception e) {
            log.error("Error ingesting eCFR data for title {}: {}", titleNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to ingest eCFR data", e);
        }
    }
    
    @Async
    public CompletableFuture<List<EcfrDTO>> ingestTitles(List<String> titleNumbers, LocalDate effectiveDate) {
        List<CompletableFuture<List<EcfrDTO>>> futures = new ArrayList<>();
        
        for (List<String> batch : partition(titleNumbers, batchSize)) {
            CompletableFuture<List<EcfrDTO>> future = CompletableFuture.supplyAsync(() -> {
                List<EcfrDTO> batchResults = new ArrayList<>();
                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, EcfrDTO.class);
                int bulkCount = 0;
                
                for (String titleNumber : batch) {
                    try {
                        EcfrDTO result = ingestTitle(titleNumber, effectiveDate);
                        if (result != null) {
                            batchResults.add(result);
                            bulkOps.insert(result);
                            bulkCount++;
                            
                            if (bulkCount >= bulkSize) {
                                bulkOps.execute();
                                bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, EcfrDTO.class);
                                bulkCount = 0;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Failed to ingest title {}: {}", titleNumber, e.getMessage());
                    }
                }
                
                if (bulkCount > 0) {
                    bulkOps.execute();
                }
                
                return batchResults;
            }, executorService);
            
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }
    
    private String buildUrl(String titleNumber, LocalDate effectiveDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%s/full/%s/title-%s.xml",
            baseUrl,
            effectiveDate.format(formatter),
            titleNumber);
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    @Async
    @Transactional
    public CompletableFuture<EcfrDTO> ingestEcfrData(String xmlContent) {
        log.info("Starting eCFR data ingestion from XML content");
        try {
            // Validate XML content
            if (xmlContent == null || xmlContent.trim().isEmpty()) {
                throw new IllegalArgumentException("XML content cannot be null or empty");
            }

            // Parse XML content
            EcfrDTO ecfrData = xmlParser.parseEcfrXmlFromString(xmlContent);
            
            // Validate parsed data
            if (!validationService.validateEcfrData(ecfrData)) {
                throw new RuntimeException("Invalid eCFR data structure");
            }

            // Extract title number from the data
            String titleNumber = extractTitleNumber(ecfrData);
            if (titleNumber == null) {
                throw new RuntimeException("Could not extract title number from eCFR data");
            }

            // Check for existing documents
            List<EcfrDTO> existingDocs = repository.findByTitleNumber(titleNumber);
            if (!existingDocs.isEmpty()) {
                // Use the most recent document as a reference
                EcfrDTO mostRecentDoc = existingDocs.get(existingDocs.size() - 1);
                // Update any necessary fields from the existing document
                if (mostRecentDoc.getHeader() != null) {
                    ecfrData.getHeader().setEffectiveDate(mostRecentDoc.getHeader().getEffectiveDate());
                }
            }

            // Save the new document
            EcfrDTO savedDoc = repository.save(ecfrData);
            return CompletableFuture.completedFuture(savedDoc);
        } catch (Exception e) {
            log.error("Error ingesting eCFR data: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void processParagraphs(EcfrDTO ecfrData) {
        if (ecfrData.getText() != null && 
            ecfrData.getText().getBody() != null && 
            ecfrData.getText().getBody().getEcfrbrws() != null) {
            
            String titleNumber = ecfrData.getText().getBody().getEcfrbrws().getTitle();
            
            // Process divisions
            if (ecfrData.getDivisions() != null) {
                for (DivisionDTO division : ecfrData.getDivisions()) {
                    processDivisionParagraphs(division, titleNumber);
                }
            }
        }
    }
    
    private void processDivisionParagraphs(DivisionDTO division, String titleNumber) {
        if (division.getParagraphs() != null) {
            for (ParagraphDTO paragraphDTO : division.getParagraphs()) {
                Paragraph paragraph = new Paragraph();
                
                // Set basic information
                paragraph.setNodeId(paragraphDTO.getNodeId());
                paragraph.setNumber(paragraphDTO.getNumber());
                paragraph.setContent(paragraphDTO.getContent());
                paragraph.setTitleNumber(titleNumber);
                paragraph.setPartNumber(division.getNumber());
                
                // Set formatting
                paragraph.setIndentationLevel(paragraphDTO.getIndentationLevel());
                paragraph.setClasses(paragraphDTO.getClasses());
                paragraph.setAttributes(paragraphDTO.getAttributes());
                
                // Set metadata
                paragraph.setWordCount(countWords(paragraphDTO.getContent()));
                paragraph.setIsIndented(paragraphDTO.getIndentationLevel() > 0);
                paragraph.setIsBold(paragraphDTO.isBold());
                paragraph.setIsItalic(paragraphDTO.isItalic());
                paragraph.setIsUnderlined(paragraphDTO.isUnderlined());
                
                // Process nested elements
                processParagraphCitations(paragraph, paragraphDTO);
                processParagraphGraphics(paragraph, paragraphDTO);
                processParagraphFootnotes(paragraph, paragraphDTO);
                
                // Generate formatted content
                paragraph.setFormattedContent(generateFormattedContent(paragraph));
                
                // Save paragraph
                mongoTemplate.save(paragraph, "paragraphs");
            }
        }
        
        // Process sub-divisions recursively
        if (division.getSubDivisions() != null) {
            for (DivisionDTO subDivision : division.getSubDivisions()) {
                processDivisionParagraphs(subDivision, titleNumber);
            }
        }
    }
    
    private void processParagraphCitations(Paragraph paragraph, ParagraphDTO paragraphDTO) {
        if (paragraphDTO.getCitations() != null) {
            List<Paragraph.Citation> citations = new ArrayList<>();
            for (CitationDTO citationDTO : paragraphDTO.getCitations()) {
                Paragraph.Citation citation = new Paragraph.Citation();
                citation.setType(citationDTO.getType());
                citation.setContent(citationDTO.getContent());
                citation.setReference(citationDTO.getReference());
                citation.setDate(citationDTO.getDate());
                citations.add(citation);
            }
            paragraph.setCitations(citations);
        }
    }
    
    private void processParagraphGraphics(Paragraph paragraph, ParagraphDTO paragraphDTO) {
        if (paragraphDTO.getGraphics() != null) {
            List<Paragraph.Graphic> graphics = new ArrayList<>();
            for (GraphicDTO graphicDTO : paragraphDTO.getGraphics()) {
                Paragraph.Graphic graphic = new Paragraph.Graphic();
                graphic.setType(graphicDTO.getType());
                graphic.setSrc(graphicDTO.getSrc());
                graphic.setAlt(graphicDTO.getAlt());
                graphic.setCaption(graphicDTO.getCaption());
                graphic.setAttributes(graphicDTO.getAttributes());
                graphics.add(graphic);
            }
            paragraph.setGraphics(graphics);
        }
    }
    
    private void processParagraphFootnotes(Paragraph paragraph, ParagraphDTO paragraphDTO) {
        if (paragraphDTO.getFootnotes() != null) {
            List<Paragraph.Footnote> footnotes = new ArrayList<>();
            for (FootnoteDTO footnoteDTO : paragraphDTO.getFootnotes()) {
                Paragraph.Footnote footnote = new Paragraph.Footnote();
                footnote.setNumber(footnoteDTO.getNumber());
                footnote.setContent(footnoteDTO.getContent());
                footnote.setReference(footnoteDTO.getReference());
                footnotes.add(footnote);
            }
            paragraph.setFootnotes(footnotes);
        }
    }
    
    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.split("\\s+").length;
    }
    
    private String generateFormattedContent(Paragraph paragraph) {
        StringBuilder formatted = new StringBuilder();
        
        // Add indentation
        for (int i = 0; i < paragraph.getIndentationLevel(); i++) {
            formatted.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        
        // Add paragraph number if present
        if (paragraph.getNumber() != null) {
            formatted.append("<span class=\"paragraph-number\">")
                    .append(paragraph.getNumber())
                    .append("</span> ");
        }
        
        // Add content with formatting
        String content = paragraph.getContent();
        if (paragraph.isBold()) {
            content = "<strong>" + content + "</strong>";
        }
        if (paragraph.isItalic()) {
            content = "<em>" + content + "</em>";
        }
        if (paragraph.isUnderlined()) {
            content = "<u>" + content + "</u>";
        }
        formatted.append(content);
        
        // Add citations
        if (!paragraph.getCitations().isEmpty()) {
            formatted.append("<div class=\"citations\">");
            for (Paragraph.Citation citation : paragraph.getCitations()) {
                formatted.append("<div class=\"citation ").append(citation.getType().toLowerCase()).append("\">");
                formatted.append(citation.getContent());
                if (citation.getDate() != null) {
                    formatted.append(" (").append(citation.getDate()).append(")");
                }
                formatted.append("</div>");
            }
            formatted.append("</div>");
        }
        
        // Add graphics
        if (!paragraph.getGraphics().isEmpty()) {
            formatted.append("<div class=\"graphics\">");
            for (Paragraph.Graphic graphic : paragraph.getGraphics()) {
                formatted.append("<div class=\"graphic ").append(graphic.getType().toLowerCase()).append("\">");
                formatted.append("<img src=\"").append(graphic.getSrc()).append("\" alt=\"").append(graphic.getAlt()).append("\">");
                if (graphic.getCaption() != null) {
                    formatted.append("<div class=\"caption\">").append(graphic.getCaption()).append("</div>");
                }
                formatted.append("</div>");
            }
            formatted.append("</div>");
        }
        
        // Add footnotes
        if (!paragraph.getFootnotes().isEmpty()) {
            formatted.append("<div class=\"footnotes\">");
            for (Paragraph.Footnote footnote : paragraph.getFootnotes()) {
                formatted.append("<div class=\"footnote\">");
                formatted.append("<sup>").append(footnote.getNumber()).append("</sup> ");
                formatted.append(footnote.getContent());
                formatted.append("</div>");
            }
            formatted.append("</div>");
        }
        
        return formatted.toString();
    }

    private String extractTitleNumber(EcfrDTO ecfrData) {
        // First try to get title number from Ecfrbrws element
        if (ecfrData.getText() != null && 
            ecfrData.getText().getBody() != null && 
            ecfrData.getText().getBody().getEcfrbrws() != null) {
            String title = ecfrData.getText().getBody().getEcfrbrws().getTitle();
            if (title != null && !title.trim().isEmpty()) {
                return title;
            }
        }

        // If not found in Ecfrbrws, try to get from DIV1 element's N attribute
        if (ecfrData.getDivisions() != null && !ecfrData.getDivisions().isEmpty()) {
            for (DivisionDTO division : ecfrData.getDivisions()) {
                if ("TITLE".equals(division.getType()) && division.getNumber() != null) {
                    return division.getNumber();
                }
            }
        }

        return null;
    }

    @Async
    public CompletableFuture<Integer> ingestDocuments(List<EcfrDocument> documents) {
        AtomicInteger totalProcessed = new AtomicInteger(0);
        List<CompletableFuture<Integer>> batchFutures = new ArrayList<>();
        
        try {
            // Split documents into batches
            for (int i = 0; i < documents.size(); i += bulkSize) {
                int endIndex = Math.min(i + bulkSize, documents.size());
                List<EcfrDocument> batch = documents.subList(i, endIndex);
                
                CompletableFuture<Integer> batchFuture = batchIngestionService.processBatch(batch);
                batchFutures.add(batchFuture);
            }
            
            // Wait for all batches to complete
            CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    int processed = batchFutures.stream()
                        .mapToInt(future -> future.join())
                        .sum();
                    totalProcessed.set(processed);
                    log.info("Completed processing {} documents", processed);
                })
                .join();
                
            return CompletableFuture.completedFuture(totalProcessed.get());
            
        } catch (Exception e) {
            log.error("Error during document ingestion: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<Void> deleteDocuments(List<String> documentIds) {
        try {
            batchIngestionService.deleteBatch(documentIds);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error during document deletion: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public EcfrDTO findByTitleNumber(String titleNumber) {
        List<EcfrDTO> documents = repository.findByTitleNumber(titleNumber);
        if (documents.isEmpty()) {
            return null;
        }
        return documents.get(documents.size() - 1); // Return the most recent document
    }
} 