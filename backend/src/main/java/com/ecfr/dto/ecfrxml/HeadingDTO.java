package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a heading in the eCFR document.
 * Headings provide hierarchical structure to the document.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeadingDTO extends BaseDTO {
    /**
     * The heading content.
     * Required field.
     */
    @NotBlank(message = "Heading content is required")
    @JacksonXmlProperty(localName = "HED")
    private String content;

    /**
     * The level of the heading (1-6).
     */
    @JacksonXmlProperty(localName = "LEVEL")
    private String level;

    /**
     * The type of heading.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The source of the heading.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 