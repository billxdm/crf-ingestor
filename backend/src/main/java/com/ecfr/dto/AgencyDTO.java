package com.ecfr.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AgencyDTO {
    private String name;
    
    @JsonProperty("short_name")
    private String shortName;
    
    @JsonProperty("display_name")
    private String displayName;
    
    private String description;
    private String website;
    
    @JsonProperty("parent_agency")
    private String parentAgency;
    
    @JsonProperty("sortable_name")
    private String sortableName;
    
    private String slug;
    
    @JsonProperty("cfr_references")
    private List<CfrReference> cfrReferences;
    
    @Data
    public static class CfrReference {
        private int title;
        private String chapter;
    }
} 