package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.repository.EcfrRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EcfrXmlServiceTest {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlServiceTest.class);

    @Autowired
    private EcfrXmlService ecfrXmlService;

    @Autowired
    private EcfrIngestionService ingestionService;

    @Autowired
    private EcfrRepository repository;

    @Test
    public void testXmlReconstruction() throws IOException {
        // Read original XML file
        String originalXml = Files.readString(Paths.get("/Users/billxiong/412.xml"));
        assertNotNull(originalXml, "Original XML content should not be null");
        assertFalse(originalXml.isEmpty(), "Original XML content should not be empty");

        // Ingest the XML
        EcfrDTO ecfrData = ingestionService.ingestEcfrData(originalXml).join();
        assertNotNull(ecfrData, "Ingested ECFR data should not be null");

        // Extract title number
        String titleNumber = ecfrData.getText().getBody().getEcfrbrws().getTitle();
        assertNotNull(titleNumber, "Title number should not be null");
        assertEquals("3", titleNumber, "Title number should be 3");

        // Reconstruct XML
        String reconstructedXml = ecfrXmlService.reconstructXml(titleNumber);
        assertNotNull(reconstructedXml, "Reconstructed XML should not be null");
        assertFalse(reconstructedXml.isEmpty(), "Reconstructed XML should not be empty");

        // Normalize both XML strings for comparison
        String normalizedOriginal = normalizeXml(originalXml);
        String normalizedReconstructed = normalizeXml(reconstructedXml);

        // Compare the normalized XML strings
        assertEquals(normalizedOriginal, normalizedReconstructed, "Reconstructed XML should match original XML");

        // Verify the reconstructed XML contains key elements
        assertTrue(reconstructedXml.contains("<ECFR>"), "Reconstructed XML should contain ECFR root element");
        assertTrue(reconstructedXml.contains("<HEADER>"), "Reconstructed XML should contain HEADER element");
        assertTrue(reconstructedXml.contains("<TEXT>"), "Reconstructed XML should contain TEXT element");
        assertTrue(reconstructedXml.contains("<BODY>"), "Reconstructed XML should contain BODY element");
        assertTrue(reconstructedXml.contains("<ECFRBRWS>"), "Reconstructed XML should contain ECFRBRWS element");
        
        // Log some information about the processed data
        log.info("Successfully processed title {}", titleNumber);
        if (ecfrData.getDivisions() != null) {
            log.info("Number of divisions: {}", ecfrData.getDivisions().size());
            ecfrData.getDivisions().forEach(division -> {
                log.info("Division: type={}, number={}, head={}", 
                    division.getType(), 
                    division.getNumber(),
                    division.getHead());
            });
        }
    }

    private String normalizeXml(String xml) {
        // Remove XML declaration if present
        xml = xml.replaceFirst("<\\?xml[^>]*\\?>", "");
        
        // Remove whitespace between tags
        xml = xml.replaceAll(">\\s+<", "><");
        
        // Remove leading/trailing whitespace
        xml = xml.trim();
        
        // Split into lines and sort
        List<String> lines = Arrays.asList(xml.split(">"));
        lines = lines.stream()
            .map(line -> line.trim() + ">")
            .filter(line -> !line.equals(">"))
            .collect(Collectors.toList());
        
        // Join back together
        return String.join("", lines);
    }
} 