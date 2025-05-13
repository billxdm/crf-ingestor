package com.ecfr.script;

import com.ecfr.dto.ecfrxml.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProcessTitle43Xml {
    private static final Logger logger = LoggerFactory.getLogger(ProcessTitle43Xml.class);
    private static final String XML_FILE_PATH = "/Users/billxiong/3213.xml";

    @Autowired
    private MongoTemplate mongoTemplate;

    public void processXmlFile() {
        try {
            // Read XML file
            String xmlContent = new String(Files.readAllBytes(Paths.get(XML_FILE_PATH)));
            logger.info("Successfully read XML file from: {}", XML_FILE_PATH);

            // Initialize XML mapper
            XmlMapper xmlMapper = new XmlMapper();
            
            // Parse XML to EcfrbrwsDTO
            EcfrbrwsDTO ecfrbrws = xmlMapper.readValue(xmlContent, EcfrbrwsDTO.class);
            logger.info("Successfully parsed XML to EcfrbrwsDTO");

            // Save to MongoDB
            saveToMongoDB(ecfrbrws);
            logger.info("Successfully saved data to MongoDB");

        } catch (IOException e) {
            logger.error("Error processing XML file: {}", e.getMessage(), e);
        }
    }

    private void saveToMongoDB(EcfrbrwsDTO ecfrbrws) {
        try {
            // Save main document
            mongoTemplate.save(ecfrbrws, "ecfrbrws");

            // Process and save divisions
            if (ecfrbrws.getDivisions() != null) {
                for (DivisionDTO division : ecfrbrws.getDivisions()) {
                    saveDivision(division);
                }
            }

            logger.info("Successfully saved all data to MongoDB");
        } catch (Exception e) {
            logger.error("Error saving to MongoDB: {}", e.getMessage(), e);
        }
    }

    private void saveDivision(DivisionDTO division) {
        try {
            // Save division
            mongoTemplate.save(division, "divisions");

            // Save nested elements
            if (division.getParagraphs() != null) {
                for (ParagraphDTO paragraph : division.getParagraphs()) {
                    mongoTemplate.save(paragraph, "paragraphs");
                }
            }

            if (division.getTables() != null) {
                for (TableDTO table : division.getTables()) {
                    mongoTemplate.save(table, "tables");
                }
            }

            if (division.getGraphics() != null) {
                for (GraphicDTO graphic : division.getGraphics()) {
                    mongoTemplate.save(graphic, "graphics");
                }
            }

            // Save authority if present
            if (division.getAuthority() != null) {
                mongoTemplate.save(division.getAuthority(), "authorities");
            }

            if (division.getExtracts() != null) {
                for (ExtractDTO extract : division.getExtracts()) {
                    mongoTemplate.save(extract, "extracts");
                }
            }

            // Recursively process sub-divisions
            if (division.getSubDivisions() != null) {
                for (DivisionDTO subDivision : division.getSubDivisions()) {
                    saveDivision(subDivision);
                }
            }
        } catch (Exception e) {
            logger.error("Error saving division {}: {}", division.getId(), e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        // This can be used to run the script directly
        ProcessTitle43Xml processor = new ProcessTitle43Xml();
        processor.processXmlFile();
    }
} 