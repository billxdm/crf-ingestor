package com.ecfr.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "agencies")
public class Agency {
    @Id
    private String id;
    
    // Basic Information
    private String name;
    private String code;
    private String shortName;
    private String displayName;
    private String description;
    private String website;
    private String parentAgency;
    private String slug;
    private String sortableName;
    private LocalDateTime lastUpdated;
    
    // CFR References
    private List<CfrReference> cfrReferences;
    
    // Regulation Statistics
    private int totalRegulations;
    private long totalWordCount;
    private String checksum;
    
    // Analysis Metrics
    private double averageComplexityScore;
    private int regulationCount;
    private long totalHistoricalChanges;
    private Map<String, Integer> regulationCountByYear;
    private Map<String, Integer> wordCountByYear;
    
    // Custom Analysis Fields
    private Map<String, Object> customMetrics;
    
    // Top Keywords and Phrases
    private List<KeywordFrequency> topKeywords;
    private List<KeywordFrequency> topPhrases;
    
    // Historical Analysis
    private List<AgencySnapshot> historicalSnapshots;
    
    @Data
    public static class CfrReference {
        private int title;
        private String chapter;
    }
    
    @Data
    public static class KeywordFrequency {
        private String keyword;
        private int frequency;
        private double percentage;
    }
    
    @Data
    public static class AgencySnapshot {
        private LocalDateTime timestamp;
        private int regulationCount;
        private long wordCount;
        private double averageComplexity;
        private Map<String, Object> metrics;
    }
} 