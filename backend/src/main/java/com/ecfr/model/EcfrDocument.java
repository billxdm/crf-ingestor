package com.ecfr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "ecfr_documents")
public class EcfrDocument {
    @Id
    private String id;
    
    private String titleNumber;
    private String chapterNumber;
    private String partNumber;
    private String sectionNumber;
    private String agency;
    private String sectionHeading;
    private String fullText;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> changeTypes;
    private Integer wordCount;
    private String checksum;
    private Integer structureIndex;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public String getTitleNumber() { return titleNumber; }
    public String getChapterNumber() { return chapterNumber; }
    public String getPartNumber() { return partNumber; }
    public String getSectionNumber() { return sectionNumber; }
    public String getAgency() { return agency; }
    public String getSectionHeading() { return sectionHeading; }
    public String getFullText() { return fullText; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public List<String> getChangeTypes() { return changeTypes; }
    public Integer getWordCount() { return wordCount; }
    public String getChecksum() { return checksum; }
    public Integer getStructureIndex() { return structureIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
} 