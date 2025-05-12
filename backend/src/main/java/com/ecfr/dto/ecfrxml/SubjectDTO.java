package com.ecfr.dto.ecfrxml;

import lombok.Data;
import java.util.List;

@Data
public class SubjectDTO {
    private String id;
    private String name;
    private String type;
    private List<String> keywords;
    private String description;
} 