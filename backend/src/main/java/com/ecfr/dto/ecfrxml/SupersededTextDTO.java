package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO representing superseded text in the eCFR document.
 * This class handles text that has been superseded by newer content.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupersededTextDTO extends BaseDTO {
    /**
     * The superseded text content.
     * Required field.
     */
    @NotBlank(message = "Superseded text content is required")
    @JacksonXmlProperty(localName = "SUPERSED")
    private String content;

    /**
     * The effective date of the supersession in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Effective date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;

    /**
     * The amendment date in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Amendment date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "AMDDATE")
    private String amendmentDate;

    /**
     * List of notes associated with the superseded text.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    /**
     * The source of the superseded text.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 