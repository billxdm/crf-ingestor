package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.dto.ecfrxml.DivisionDTO;
import com.ecfr.dto.ecfrxml.PartDTO;
import com.ecfr.dto.ecfrxml.SubtitleDTO;
import com.ecfr.model.Paragraph;
import com.ecfr.repository.EcfrRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProcessXmlFileTest {
    private static final Logger log = LoggerFactory.getLogger(ProcessXmlFileTest.class);

    @Autowired
    private XmlParserService xmlParserService;

    @Autowired
    private EcfrIngestionService ingestionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EcfrRepository repository;

    @Test
    public void testProcessXmlFile() throws IOException {
        // Read the XML file
        String xmlContent = Files.readString(Paths.get("/Users/billxiong/412.xml"));
        assertNotNull(xmlContent, "XML content should not be null");
        assertFalse(xmlContent.isEmpty(), "XML content should not be empty");

        // Process the XML file
        EcfrDTO ecfrData = ingestionService.ingestEcfrData(xmlContent).join();
        assertNotNull(ecfrData, "ECFR data should not be null");

        // Extract title number
        String titleNumber = ecfrData.getText().getBody().getEcfrbrws().getTitle();
        assertNotNull(titleNumber, "Title number should not be null");
        assertEquals("3", titleNumber, "Title number should be 3");

        // Verify the data was stored in MongoDB
        List<EcfrDTO> storedData = repository.findByTitleNumber(titleNumber);
        assertNotNull(storedData, "Data should be stored in MongoDB");
        assertFalse(storedData.isEmpty(), "Should have stored documents in MongoDB");
        assertEquals(titleNumber, storedData.get(0).getText().getBody().getEcfrbrws().getTitle(), 
            "Stored title number should match");

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

    private void verifyParsedStructure(EcfrDTO ecfrDTO) {
        log.info("Verifying parsed structure...");
        
        // Verify title information
        if (ecfrDTO.getText() != null && ecfrDTO.getText().getBody() != null) {
            log.info("Title: {}", ecfrDTO.getText().getBody().getEcfrbrws().getTitle());
        }

        // Verify divisions
        if (ecfrDTO.getDivisions() != null) {
            log.info("Number of divisions: {}", ecfrDTO.getDivisions().size());
            for (DivisionDTO division : ecfrDTO.getDivisions()) {
                log.info("Division: type={}, number={}, head={}", 
                    division.getType(), 
                    division.getNumber(),
                    division.getHead());
                
                // Verify sub-divisions
                if (division.getSubDivisions() != null) {
                    log.info("Number of sub-divisions: {}", division.getSubDivisions().size());
                    for (DivisionDTO subDivision : division.getSubDivisions()) {
                        log.info("Sub-division: type={}, number={}, head={}", 
                            subDivision.getType(),
                            subDivision.getNumber(),
                            subDivision.getHead());
                    }
                }
            }
        }
    }

    private void verifyMongoDBData(EcfrDTO ecfrDTO) {
        log.info("Verifying MongoDB data...");
        
        // Query the database to verify the data
        String titleNumber = ecfrDTO.getText().getBody().getEcfrbrws().getTitle();
        EcfrDTO storedDTO = ingestionService.findByTitleNumber(titleNumber);
        
        if (storedDTO != null) {
            log.info("Found document in MongoDB for title {}", titleNumber);
            
            // Verify the stored structure matches the parsed structure
            if (storedDTO.getText() != null && 
                storedDTO.getText().getBody() != null && 
                storedDTO.getText().getBody().getEcfrbrws() != null) {
                
                log.info("Stored title: {}", 
                    storedDTO.getText().getBody().getEcfrbrws().getTitle());
                
                // Verify divisions
                if (storedDTO.getDivisions() != null) {
                    log.info("Stored divisions count: {}", 
                        storedDTO.getDivisions().size());
                }
            }
        } else {
            log.error("No document found in MongoDB for title {}", titleNumber);
        }
    }
} 