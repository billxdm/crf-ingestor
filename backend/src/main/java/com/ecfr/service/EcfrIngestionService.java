package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.repository.EcfrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
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
                throw new RuntimeException("Could not determine title number from XML content");
            }

            // Check if document already exists
            Optional<EcfrDTO> existingDoc = repository.findByTitleNumber(titleNumber);
            if (existingDoc.isPresent()) {
                log.info("Document for title {} already exists, updating...", titleNumber);
                ecfrData.setId(existingDoc.get().getId());
            }
            
            // Save to MongoDB
            EcfrDTO savedData = repository.save(ecfrData);
            log.info("Successfully ingested eCFR data for title {}", titleNumber);
            return CompletableFuture.completedFuture(savedData);
        } catch (Exception e) {
            log.error("Error ingesting eCFR data: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private String extractTitleNumber(EcfrDTO ecfrData) {
        if (ecfrData.getText() != null && 
            ecfrData.getText().getBody() != null && 
            ecfrData.getText().getBody().getEcfrbrws() != null) {
            return ecfrData.getText().getBody().getEcfrbrws().getTitle();
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
} 