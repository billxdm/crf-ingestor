package com.ecfr.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "cfr_titles")
public class CfrTitle {
    @Id
    private String id;
    
    // Basic Information
    private int titleNumber;
    private String name;
    private String description;
    private LocalDateTime lastUpdated;
    private String checksum;
    
    // XML Structure
    private String nodeId;  // e.g., "1:1"
    private String type = "TITLE";  // Always "TITLE" for DIV1
    
    // Agency responsible for this title
    private String agencyId;
    private String agencyName;
    
    // Hierarchical Structure
    private List<Subtitle> subtitles;  // DIV2
    private List<Chapter> chapters;    // DIV3
    
    // Statistics
    private int totalParts;
    private int totalSections;
    private long totalWordCount;
    private Map<String, Integer> partCountByYear;
    private Map<String, Integer> sectionCountByYear;
    
    // Analysis
    private double averageComplexityScore;
    private List<KeywordFrequency> topKeywords;
    private List<KeywordFrequency> topPhrases;
    
    @Data
    public static class Subtitle {
        private String number;
        private String nodeId;
        private String name;
        private List<Chapter> chapters;
    }
    
    @Data
    public static class Chapter {
        private String number;
        private String nodeId;
        private String name;
        private List<Subchapter> subchapters;
    }
    
    @Data
    public static class Subchapter {
        private String letter;
        private String nodeId;
        private String name;
        private List<Part> parts;
    }
    
    @Data
    public static class Part {
        private String number;
        private String nodeId;
        private String name;
        private List<Subpart> subparts;
        private List<Section> sections;
    }
    
    @Data
    public static class Subpart {
        private String letter;
        private String nodeId;
        private String name;
        private List<String> sectionNumbers;
    }
    
    @Data
    public static class Section {
        private String number;
        private String nodeId;
        private String name;
        private String content;
        private List<String> authorities;
        private List<String> source;
        private List<String> crossReferences;
    }
    
    @Data
    public static class KeywordFrequency {
        private String keyword;
        private int frequency;
        private double percentage;
    }
} 