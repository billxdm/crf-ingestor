package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO representing an extract in the eCFR document.
 * Supports nested elements and hierarchical structure.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtractDTO extends BaseDTO {
    /**
     * The extract content.
     * Required field.
     */
    @NotBlank(message = "Extract content is required")
    @JacksonXmlProperty(localName = "EXTRACT")
    private String content;

    /**
     * The type of extract.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * List of paragraphs within the extract.
     */
    @JacksonXmlProperty(localName = "P")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ParagraphDTO> paragraphs;

    /**
     * List of divisions within the extract.
     */
    @JacksonXmlProperty(localName = "DIV")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> divisions;

    /**
     * List of tables within the extract.
     */
    @JacksonXmlProperty(localName = "TABLE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableDTO> tables;

    /**
     * List of graphics within the extract.
     */
    @JacksonXmlProperty(localName = "GRAPHIC")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphicDTO> graphics;

    /**
     * List of notes within the extract.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    /**
     * List of citations within the extract.
     */
    @JacksonXmlProperty(localName = "CITA")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CitationDTO> citations;

    /**
     * List of cross references within the extract.
     */
    @JacksonXmlProperty(localName = "CROSSREF")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CrossReferenceDTO> crossReferences;

    /**
     * The source of the extract.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 