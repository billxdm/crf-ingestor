package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing an editorial note in the eCFR document.
 * These notes provide additional context or clarification about the content.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EditorialNoteDTO extends BaseDTO {
    /**
     * The editorial note content.
     * Required field.
     */
    @NotBlank(message = "Editorial note content is required")
    @JacksonXmlProperty(localName = "EDNOTE")
    private String content;

    /**
     * The type of editorial note.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The source of the editorial note.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 