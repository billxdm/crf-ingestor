package com.ecfr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "regulations")
public class Regulation {
    @Id
    private String id;
    
    // Basic Information
    private String title;
    private String agency;
    private String agencyCode;
    private String content;
    private String partNumber;
    private String sectionNumber;
    private String citation;
    
    // Metadata
    private LocalDateTime effectiveDate;
    private LocalDateTime lastUpdated;
    private String status; // ACTIVE, SUPERSEDED, RESCINDED
    private List<String> keywords;
    private List<String> references; // References to other regulations
    
    // Analysis Metrics
    private int wordCount;
    private int sentenceCount;
    private int paragraphCount;
    private String checksum;
    private double complexityScore;
    private Map<String, Integer> wordFrequency; // Word frequency analysis
    
    // Historical version tracking
    private List<RegulationVersion> versions;
    
    // Custom Analysis Fields
    private Map<String, Object> customMetrics; // For storing custom analysis results
    
    @Data
    public static class RegulationVersion {
        private String versionId;
        private String content;
        private LocalDateTime effectiveDate;
        private String checksum;
        private int wordCount;
        private String changeDescription;
        private String changeType; // ADDITION, MODIFICATION, DELETION
        private Map<String, Object> diffMetrics; // Store diff analysis results
    }
} 