package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a cross reference in the eCFR document.
 * Cross references link to other parts of the document.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrossReferenceDTO extends BaseDTO {
    /**
     * The cross reference content.
     * Required field.
     */
    @NotBlank(message = "Cross reference content is required")
    @JacksonXmlProperty(localName = "CROSSREF")
    private String crossReference;

    /**
     * The type of cross reference.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The target of the cross reference.
     */
    @JacksonXmlProperty(localName = "TARGET")
    private String target;

    /**
     * The source of the cross reference.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    /**
     * The effective date of the cross reference.
     */
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;
} 