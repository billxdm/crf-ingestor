package com.ecfr.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "cfr_sections")
public class CfrSection {
    @Id
    private String id;
    
    // Basic Information
    private int titleNumber;
    private String partNumber;
    private String sectionNumber;
    private String name;
    private LocalDateTime lastUpdated;
    private String checksum;
    
    // XML Structure
    private String nodeId;  // e.g., "5:1.0.1.2.12.0.1.1"
    private String type = "SECTION";  // Always "SECTION" for DIV8
    
    // Agency responsible for this section
    private String agencyId;
    private String agencyName;
    
    // Section metadata
    private String subpart;
    private List<String> authorities;
    private List<String> source;
    private List<String> crossReferences;
    
    // Content Structure
    private List<Paragraph> paragraphs;
    private List<Citation> citations;
    private List<Table> tables;
    private List<Abbreviation> abbreviations;
    private List<Index> indexes;
    
    // Content analysis
    private int wordCount;
    private double complexityScore;
    private List<KeywordFrequency> keywords;
    private List<KeywordFrequency> phrases;
    
    // Historical data
    private List<SectionHistory> history;
    
    @Data
    public static class Paragraph {
        private String content;
        private String type;  // P, PSPACE, FP, P-1, etc.
        private List<Paragraph> subParagraphs;  // For nested paragraphs
        private String number;  // For numbered paragraphs like (a), (1), (i)
        private boolean italicized;  // For <I> tags
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<Paragraph> getSubParagraphs() { return subParagraphs; }
        public void setSubParagraphs(List<Paragraph> subParagraphs) { this.subParagraphs = subParagraphs; }
        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
        public boolean isItalicized() { return italicized; }
        public void setItalicized(boolean italicized) { this.italicized = italicized; }
    }
    
    @Data
    public static class Citation {
        private String type;  // N for note, etc.
        private String content;
        private LocalDateTime effectiveDate;
        private String documentNumber;
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getEffectiveDate() { return effectiveDate; }
        public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    }
    
    @Data
    public static class KeywordFrequency {
        private String keyword;
        private int frequency;
        private double percentage;
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public int getFrequency() { return frequency; }
        public void setFrequency(int frequency) { this.frequency = frequency; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
    
    @Data
    public static class SectionHistory {
        private LocalDateTime effectiveDate;
        private String content;
        private String changeDescription;
        private String documentNumber;
        private String documentType;
        public LocalDateTime getEffectiveDate() { return effectiveDate; }
        public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getChangeDescription() { return changeDescription; }
        public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
        public String getDocumentNumber() { return documentNumber; }
        public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
    }

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }
    public void setName(String name) { this.name = name; }
    public void setParagraphs(List<Paragraph> paragraphs) { this.paragraphs = paragraphs; }
    public void setCitations(List<Citation> citations) { this.citations = citations; }
    public void setTables(List<Table> tables) { this.tables = tables; }
    public void setAbbreviations(List<Abbreviation> abbreviations) { this.abbreviations = abbreviations; }
    public void setIndexes(List<Index> indexes) { this.indexes = indexes; }
} 