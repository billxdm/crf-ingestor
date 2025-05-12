package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a footnote in the eCFR document.
 * Footnotes provide additional information or references.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FootnoteDTO extends BaseDTO {
    /**
     * The footnote content.
     * Required field.
     */
    @NotBlank(message = "Footnote content is required")
    @JacksonXmlProperty(localName = "FTNOTE")
    private String footnote;

    /**
     * The text content of the footnote.
     */
    @JacksonXmlProperty(localName = "TEXT")
    private String text;

    /**
     * The type of footnote.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The source of the footnote.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    /**
     * The effective date of the footnote.
     */
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;
} 