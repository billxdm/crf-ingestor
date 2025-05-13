package com.ecfr.service;

import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EcfrXmlReconstructor {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlReconstructor.class);
    
    @Autowired
    private EcfrDocumentRepository ecfrDocumentRepository;

    /**
     * Reconstructs the original XML file from MongoDB data
     * @param documentId The ID of the document in MongoDB
     * @param outputPath The path where the reconstructed XML should be saved
     * @return true if successful, false otherwise
     */
    public boolean reconstructXml(String documentId, String outputPath) {
        try {
            // Retrieve document from MongoDB
            EcfrDocument document = ecfrDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

            // Get the stored XML content
            String xmlContent = document.getXmlContent();
            if (xmlContent == null || xmlContent.isEmpty()) {
                log.error("No XML content found for document: {}", documentId);
                return false;
            }

            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(outputPath).getParent();
            if (outputDir != null) {
                Files.createDirectories(outputDir);
            }

            // Write the XML content to file
            Files.write(Paths.get(outputPath), xmlContent.getBytes());
            log.info("Successfully reconstructed XML file to: {}", outputPath);
            return true;

        } catch (Exception e) {
            log.error("Error reconstructing XML file: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Reconstructs the original XML file to its original location
     * @param documentId The ID of the document in MongoDB
     * @return true if successful, false otherwise
     */
    public boolean reconstructToOriginalLocation(String documentId) {
        try {
            EcfrDocument document = ecfrDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

            String originalPath = document.getOriginalXmlPath();
            if (originalPath == null || originalPath.isEmpty()) {
                log.error("No original path found for document: {}", documentId);
                return false;
            }

            return reconstructXml(documentId, originalPath);

        } catch (Exception e) {
            log.error("Error reconstructing XML to original location: {}", e.getMessage(), e);
            return false;
        }
    }
} 