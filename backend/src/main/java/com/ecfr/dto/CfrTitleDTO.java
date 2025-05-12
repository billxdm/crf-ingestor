package com.ecfr.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CfrTitleDTO {
    @JsonProperty("title_number")
    private int titleNumber;
    
    private String name;
    private String description;
    
    @JsonProperty("node_id")
    private String nodeId;
    
    @JsonProperty("agency_id")
    private String agencyId;
    
    @JsonProperty("agency_name")
    private String agencyName;
    
    private List<SubtitleDTO> subtitles;
    private List<ChapterDTO> chapters;
    
    @Data
    public static class SubtitleDTO {
        private String number;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        private List<ChapterDTO> chapters;
    }
    
    @Data
    public static class ChapterDTO {
        private String number;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        private List<SubchapterDTO> subchapters;
    }
    
    @Data
    public static class SubchapterDTO {
        private String letter;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        private List<PartDTO> parts;
    }
    
    @Data
    public static class PartDTO {
        private String number;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        private List<SubpartDTO> subparts;
        private List<SectionDTO> sections;
    }
    
    @Data
    public static class SubpartDTO {
        private String letter;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        
        @JsonProperty("section_numbers")
        private List<String> sectionNumbers;
    }
    
    @Data
    public static class SectionDTO {
        private String number;
        
        @JsonProperty("node_id")
        private String nodeId;
        
        private String name;
        private String content;
        private List<String> authorities;
        private List<String> source;
        
        @JsonProperty("cross_references")
        private List<String> crossReferences;
    }
} 