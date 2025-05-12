package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing a source in the eCFR document.
 * Supports hierarchical structure and nested elements.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SourceDTO extends BaseDTO {
    /**
     * The source content.
     * Required field.
     */
    @NotBlank(message = "Source content is required")
    @JacksonXmlProperty(localName = "SOURCE_CONTENT")
    private String content;

    /**
     * The type of source.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The level of the source in the hierarchy.
     */
    @JacksonXmlProperty(localName = "LEVEL")
    private String level;

    /**
     * List of sub-sources.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SourceDTO> subSources;

    /**
     * List of citations within the source.
     */
    @JacksonXmlProperty(localName = "CITA")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CitationDTO> citations;

    /**
     * List of cross references within the source.
     */
    @JacksonXmlProperty(localName = "CROSSREF")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CrossReferenceDTO> crossReferences;

    /**
     * List of notes within the source.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    /**
     * The effective date of the source.
     */
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;

    /**
     * The amendment date of the source.
     */
    @JacksonXmlProperty(localName = "AMDDATE")
    @Field("sourceAmendmentDate")
    private String amendmentDate;
} 