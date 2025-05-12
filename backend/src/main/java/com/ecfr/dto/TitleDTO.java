package com.ecfr.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "titles")
public class TitleDTO {
    @Id
    private String id;
    
    private String titleNumber;
    private String titleName;
    private List<String> agencies;
    private Integer totalParts;
    private Integer totalSections;
    private Long totalWordCount;
    private LocalDateTime lastUpdated;
    private String version;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional setters for fields that need explicit setting
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 