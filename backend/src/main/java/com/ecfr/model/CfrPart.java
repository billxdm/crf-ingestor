package com.ecfr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "cfr_parts")
public class CfrPart {
    @Id
    private String id;
    
    private int titleNumber;
    private String partNumber;
    private String name;
    private String description;
    private LocalDateTime lastUpdated;
    private String checksum;
    
    // Agency responsible for this part
    private String agencyId;
    private String agencyName;
    
    // Sections contained in this part
    private List<CfrSection> sections;
    
    // Subparts
    private List<Subpart> subparts;
    
    // Statistics
    private int totalSections;
    private long totalWordCount;
    private Map<String, Integer> sectionCountByYear;
    private Map<String, Integer> wordCountByYear;
    
    // Analysis
    private double averageComplexityScore;
    private List<KeywordFrequency> topKeywords;
    private List<KeywordFrequency> topPhrases;
    
    @Data
    public static class Subpart {
        private String letter;
        private String name;
        private String description;
        private List<String> sectionNumbers;
    }
    
    @Data
    public static class KeywordFrequency {
        private String keyword;
        private int frequency;
        private double percentage;
    }
} 