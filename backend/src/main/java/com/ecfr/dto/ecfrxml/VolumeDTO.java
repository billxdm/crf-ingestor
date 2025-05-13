package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class VolumeDTO {
    @JacksonXmlProperty(localName = "N", isAttribute = true)
    private String n;

    @JacksonXmlProperty(localName = "AMDDATE", isAttribute = true)
    private String amendmentDate;
} 