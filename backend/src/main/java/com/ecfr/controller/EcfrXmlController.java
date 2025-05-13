package com.ecfr.controller;

import com.ecfr.service.EcfrXmlReconstructor;
import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import com.ecfr.service.EcfrXmlIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/ecfr/xml")
public class EcfrXmlController {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlController.class);
    
    @Autowired
    private EcfrXmlReconstructor xmlReconstructor;
    
    @Autowired
    private EcfrDocumentRepository ecfrDocumentRepository;
    
    @Autowired
    private EcfrXmlIngestor xmlIngestor;

    @PostMapping("/reconstruct/{documentId}")
    public ResponseEntity<String> reconstructXml(
            @PathVariable String documentId,
            @RequestParam(required = false) String outputPath) {
        try {
            boolean success;
            if (outputPath != null && !outputPath.isEmpty()) {
                success = xmlReconstructor.reconstructXml(documentId, outputPath);
            } else {
                success = xmlReconstructor.reconstructToOriginalLocation(documentId);
            }

            if (success) {
                return ResponseEntity.ok("XML file reconstructed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to reconstruct XML file");
            }
        } catch (Exception e) {
            log.error("Error in reconstructXml endpoint: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test/verify")
    public ResponseEntity<?> verifyMongoData() {
        try {
            List<EcfrDocument> documents = ecfrDocumentRepository.findAll();
            if (documents.isEmpty()) {
                return ResponseEntity.ok("No documents found in MongoDB");
            }

            StringBuilder response = new StringBuilder();
            response.append("Found ").append(documents.size()).append(" documents in MongoDB:\n\n");
            
            for (EcfrDocument doc : documents) {
                response.append("Document ID: ").append(doc.getId()).append("\n");
                response.append("Original Path: ").append(doc.getOriginalXmlPath()).append("\n");
                response.append("Created At: ").append(doc.getCreatedAt()).append("\n");
                response.append("XML Content Length: ").append(doc.getXmlContent() != null ? doc.getXmlContent().length() : 0).append(" bytes\n");
                response.append("Title Number: ").append(doc.getTitleNumber()).append("\n");
                response.append("Part Number: ").append(doc.getPartNumber()).append("\n");
                response.append("Section Number: ").append(doc.getSectionNumber()).append("\n");
                response.append("Agency: ").append(doc.getAgency()).append("\n");
                response.append("Section Heading: ").append(doc.getSectionHeading()).append("\n");
                response.append("Word Count: ").append(doc.getWordCount()).append("\n");
                response.append("----------------------------------------\n");
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            log.error("Error verifying MongoDB data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test/ingest")
    public ResponseEntity<String> testIngest() {
        try {
            xmlIngestor.run();
            return ResponseEntity.ok("XML ingestion process completed. Use /test/verify to check the results.");
        } catch (Exception e) {
            log.error("Error in test ingestion: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
} 