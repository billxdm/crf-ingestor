package com.ecfr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class CfrPartDTO {
    @JsonProperty("title_number")
    private int titleNumber;
    
    @JsonProperty("part_number")
    private String partNumber;
    
    private String name;
    private String description;
    
    @JsonProperty("agency_id")
    private String agencyId;
    
    @JsonProperty("agency_name")
    private String agencyName;
    
    private List<CfrSectionDTO> sections;
    private List<SubpartDTO> subparts;
    
    @Data
    public static class SubpartDTO {
        private String letter;
        private String name;
        private String description;
        
        @JsonProperty("section_numbers")
        private List<String> sectionNumbers;
    }
} 