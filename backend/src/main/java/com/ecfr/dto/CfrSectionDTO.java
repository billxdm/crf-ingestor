package com.ecfr.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CfrSectionDTO {
    @JsonProperty("title_number")
    private int titleNumber;
    
    @JsonProperty("part_number")
    private String partNumber;
    
    @JsonProperty("section_number")
    private String sectionNumber;
    
    private String name;
    
    @JsonProperty("node_id")
    private String nodeId;
    
    @JsonProperty("agency_id")
    private String agencyId;
    
    @JsonProperty("agency_name")
    private String agencyName;
    
    private String subpart;
    private List<String> authorities;
    private List<String> source;
    
    @JsonProperty("cross_references")
    private List<String> crossReferences;
    
    private List<ParagraphDTO> paragraphs;
    private List<CitationDTO> citations;
    
    @Data
    public static class ParagraphDTO {
        private String content;
        private String type;
        
        @JsonProperty("sub_paragraphs")
        private List<ParagraphDTO> subParagraphs;
        
        private String number;
        private boolean italicized;
    }
    
    @Data
    public static class CitationDTO {
        private String type;
        private String content;
        
        @JsonProperty("effective_date")
        private LocalDateTime effectiveDate;
        
        @JsonProperty("document_number")
        private String documentNumber;
    }
} 