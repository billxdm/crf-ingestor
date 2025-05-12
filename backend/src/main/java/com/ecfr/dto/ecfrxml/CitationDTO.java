package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing a citation in the eCFR document.
 * Citations reference other documents or regulations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CitationDTO extends BaseDTO {
    /**
     * The citation content.
     * Required field.
     */
    @NotBlank(message = "Citation content is required")
    @JacksonXmlProperty(localName = "CITA")
    private String citation;

    /**
     * The type of citation.
     */
    @JacksonXmlProperty(localName = "TYPE")
    @Field("citationType")
    private String type;

    /**
     * The target of the citation.
     */
    @JacksonXmlProperty(localName = "TARGET")
    private String target;

    /**
     * The source of the citation.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    /**
     * The effective date of the citation.
     */
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;
} 