package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * DTO representing a citation in the eCFR document.
 * Citations reference other documents or regulations.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CitationDTO {
    /**
     * The type of citation.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The citation content.
     */
    private String content;

    /**
     * The reference of the citation.
     */
    private String reference;

    /**
     * The date of the citation.
     */
    private String date;

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getReference() {
        return reference;
    }
} 