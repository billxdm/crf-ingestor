package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcfrXmlIngestor implements CommandLineRunner {
    private final EcfrDocumentRepository ecfrDocumentRepository;
    private final XmlMapper xmlMapper;
    private final GridFsTemplate gridFsTemplate;

    @Override
    public void run(String... args) throws Exception {
        String xmlPath = "/Users/billxiong/412.xml";
        log.info("Loading and parsing XML file: {}", xmlPath);
        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            log.error("File not found: {}", xmlPath);
            return;
        }

        // Read the raw XML content
        String xmlContent = new String(Files.readAllBytes(xmlFile.toPath()));
        log.info("Read XML content, length: {} bytes", xmlContent.length());
        log.debug("First 500 characters of XML content: {}", xmlContent.substring(0, Math.min(500, xmlContent.length())));
        
        // Parse the XML into DTO
        EcfrDTO ecfrDTO = xmlMapper.readValue(xmlFile, EcfrDTO.class);
        log.info("Parsed EcfrDTO: {}", ecfrDTO);

        // Store XML content in GridFS
        String xmlContentId = gridFsTemplate.store(
            new ByteArrayInputStream(xmlContent.getBytes()),
            xmlFile.getName(),
            "text/xml"
        ).toString();
        log.info("Stored XML content in GridFS with ID: {}", xmlContentId);

        // Create and populate the document
        EcfrDocument document = new EcfrDocument();
        document.setEcfrDTO(ecfrDTO);
        document.setXmlContentId(xmlContentId);
        document.setOriginalXmlPath(xmlPath);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        // Extract metadata from the DTO
        if (ecfrDTO.getDivisions() != null && !ecfrDTO.getDivisions().isEmpty()) {
            DivisionDTO firstDivision = ecfrDTO.getDivisions().get(0);
            if (firstDivision != null) {
                document.setTitleNumber(firstDivision.getN());
                document.setSectionHeading(firstDivision.getHead());
            }
        }

        // Save to MongoDB
        EcfrDocument savedDocument = ecfrDocumentRepository.save(document);
        log.info("Saved EcfrDTO to MongoDB with ID: {}", savedDocument.getId());
        log.info("Document references GridFS XML content with ID: {}", savedDocument.getXmlContentId());
    }
} 