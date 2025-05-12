package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a footnote reference in the eCFR document.
 * These references link to corresponding footnotes in the text.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FootnoteReferenceDTO extends BaseDTO {
    /**
     * The footnote reference identifier.
     * Required field.
     */
    @NotBlank(message = "Footnote reference identifier is required")
    @JacksonXmlProperty(localName = "FTREF")
    private String reference;

    /**
     * The target footnote ID that this reference points to.
     */
    @JacksonXmlProperty(localName = "TARGET")
    private String targetId;

    /**
     * The type of footnote reference.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;
} 