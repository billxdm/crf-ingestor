package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a top caption in the eCFR document.
 * Top captions provide additional context or information above content.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TopCaptionDTO extends BaseDTO {
    /**
     * The top caption content.
     * Required field.
     */
    @NotBlank(message = "Top caption content is required")
    @JacksonXmlProperty(localName = "TCAP")
    private String content;

    /**
     * The type of top caption.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The source of the top caption.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 