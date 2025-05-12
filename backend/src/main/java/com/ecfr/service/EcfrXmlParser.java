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

@Service
public class EcfrXmlParser {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlParser.class);
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public EcfrXmlParser(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.xmlMapper = new XmlMapper();
        // Configure XML mapper for better performance
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        xmlMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        xmlMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, false);
        xmlMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        xmlMapper.configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true);
        xmlMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        xmlMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    }

    public EcfrDTO parseEcfrXml(String url) {
        Instant start = Instant.now();
        log.info("Starting to parse eCFR XML from URL: {}", url);
        try {
            log.info("Fetching XML content from URL...");
            String xmlContent = restTemplate.getForObject(url, String.class);
            log.info("Successfully fetched XML content, size: {} bytes", xmlContent.length());
            
            log.info("Starting XML parsing...");
            EcfrDTO result = xmlMapper.readValue(xmlContent, EcfrDTO.class);
            
            Duration duration = Duration.between(start, Instant.now());
            log.info("Successfully parsed eCFR XML in {} seconds", duration.getSeconds());
            return result;
        } catch (IOException e) {
            log.error("Error parsing eCFR XML from URL: {}", url, e);
            throw new RuntimeException("Failed to parse eCFR XML", e);
        }
    }

    public EcfrDTO parseEcfrXmlFromString(String xmlContent) {
        Instant start = Instant.now();
        log.info("Starting to parse eCFR XML from string, content size: {} bytes", xmlContent.length());
        try {
            log.info("Starting XML parsing...");
            EcfrDTO result = xmlMapper.readValue(xmlContent, EcfrDTO.class);
            
            Duration duration = Duration.between(start, Instant.now());
            log.info("Successfully parsed eCFR XML in {} seconds", duration.getSeconds());
            return result;
        } catch (IOException e) {
            log.error("Error parsing eCFR XML content", e);
            throw new RuntimeException("Failed to parse eCFR XML content", e);
        }
    }
} 