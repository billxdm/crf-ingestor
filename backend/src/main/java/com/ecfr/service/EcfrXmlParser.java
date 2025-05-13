package com.ecfr.service;

import com.ecfr.dto.ecfrxml.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

@Service
public class EcfrXmlParser {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlParser.class);
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public EcfrXmlParser(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.xmlMapper = new XmlMapper();
        // Configure XML mapper for better performance and handling of large files
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        xmlMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        xmlMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        xmlMapper.configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true);
        xmlMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        xmlMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        // Add support for large files
        xmlMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        xmlMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    }

    public EcfrDTO parseEcfrXml(String url) {
        Instant start = Instant.now();
        log.info("Starting to parse eCFR XML from URL: {}", url);
        try {
            log.info("Fetching XML content from URL...");
            String xmlContent = restTemplate.getForObject(url, String.class);
            if (xmlContent == null) {
                throw new RuntimeException("Failed to fetch XML content from URL: " + url);
            }
            log.info("Successfully fetched XML content, size: {} bytes", xmlContent.length());
            
            return parseXmlContent(xmlContent, start);
        } catch (Exception e) {
            log.error("Error parsing eCFR XML from URL: {}", url, e);
            throw new RuntimeException("Failed to parse eCFR XML", e);
        }
    }

    public EcfrDTO parseEcfrXmlFromString(String xmlContent) {
        Instant start = Instant.now();
        log.info("Starting to parse eCFR XML from string, content size: {} bytes", xmlContent.length());
        return parseXmlContent(xmlContent, start);
    }

    private EcfrDTO parseXmlContent(String xmlContent, Instant start) {
        try {
            log.info("Starting XML parsing...");
            EcfrDTO result = xmlMapper.readValue(xmlContent, EcfrDTO.class);
            
            Duration duration = Duration.between(start, Instant.now());
            log.info("Successfully parsed eCFR XML in {} seconds", duration.getSeconds());
            
            // Validate the parsed content
            if (result == null || result.getDivisions() == null) {
                throw new RuntimeException("Invalid XML structure: missing required DIV1 elements");
            }

            // Create EcfrbrwsDTO from the first DIV1 element if it doesn't exist
            if (result.getText() == null) {
                result.setText(new TextDTO());
            }
            if (result.getText().getBody() == null) {
                result.getText().setBody(new BodyDTO());
            }
            if (result.getText().getBody().getEcfrbrws() == null && !result.getDivisions().isEmpty()) {
                DivisionDTO firstDivision = result.getDivisions().get(0);
                if (firstDivision != null && "TITLE".equals(firstDivision.getType())) {
                    EcfrbrwsDTO ecfrbrws = new EcfrbrwsDTO();
                    ecfrbrws.setTitle(firstDivision.getNumber());
                    ecfrbrws.setId(firstDivision.getNumber());
                    result.getText().getBody().setEcfrbrws(ecfrbrws);
                }
            }
            
            return result;
        } catch (IOException e) {
            log.error("Error parsing eCFR XML content: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse eCFR XML content: " + e.getMessage(), e);
        }
    }

    public EcfrDTO parseEcfrXmlFromStream(InputStream inputStream) {
        Instant start = Instant.now();
        log.info("Starting to parse eCFR XML from input stream");
        try {
            String xmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            log.info("Successfully read XML content from stream, size: {} bytes", xmlContent.length());
            return parseXmlContent(xmlContent, start);
        } catch (IOException e) {
            log.error("Error reading XML from input stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read XML from input stream", e);
        }
    }
} 