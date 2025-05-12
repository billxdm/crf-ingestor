package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing an effective date note in the eCFR document.
 * These notes provide information about when specific content becomes effective.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EffectiveDateNoteDTO extends BaseDTO {
    /**
     * The effective date note content.
     * Required field.
     */
    @NotBlank(message = "Effective date note content is required")
    @JacksonXmlProperty(localName = "EFFDNOT")
    private String content;

    /**
     * The effective date in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Effective date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;

    /**
     * The source of the effective date note.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 