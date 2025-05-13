package com.ecfr.model;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private EcfrDTO ecfrDTO;
    private String effectiveDate;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String originalXmlPath;  // Store the original file path
    
    @Field("xml_content_id")
    private String xmlContentId;     // GridFS file ID for XML content
    
    // Getters
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
    public EcfrDTO getEcfrDTO() { return ecfrDTO; }
    public String getEffectiveDate() { return effectiveDate; }
    public String getOriginalXmlPath() { return originalXmlPath; }
    public String getXmlContentId() { return xmlContentId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitleNumber(String titleNumber) { this.titleNumber = titleNumber; }
    public void setChapterNumber(String chapterNumber) { this.chapterNumber = chapterNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }
    public void setAgency(String agency) { this.agency = agency; }
    public void setSectionHeading(String sectionHeading) { this.sectionHeading = sectionHeading; }
    public void setFullText(String fullText) { this.fullText = fullText; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setChangeTypes(List<String> changeTypes) { this.changeTypes = changeTypes; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public void setStructureIndex(Integer structureIndex) { this.structureIndex = structureIndex; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setEcfrDTO(EcfrDTO ecfrDTO) { this.ecfrDTO = ecfrDTO; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }
    public void setOriginalXmlPath(String originalXmlPath) { this.originalXmlPath = originalXmlPath; }
    public void setXmlContentId(String xmlContentId) { this.xmlContentId = xmlContentId; }
} 