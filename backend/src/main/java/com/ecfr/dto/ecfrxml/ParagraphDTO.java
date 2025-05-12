package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO representing a paragraph in the eCFR document.
 * Supports different paragraph types and formatting.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParagraphDTO extends BaseDTO {
    /**
     * The paragraph content.
     * Required field.
     */
    @NotBlank(message = "Paragraph content is required")
    @JacksonXmlProperty(localName = "P")
    private String content;

    /**
     * The paragraph type (P, P-1, P-2, etc.).
     */
    @Pattern(regexp = "^P(-[1-9])?$", message = "Paragraph type must be P or P-1 through P-9")
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The paragraph level.
     */
    @JacksonXmlProperty(localName = "LEVEL")
    private String level;

    /**
     * The paragraph alignment.
     */
    @JacksonXmlProperty(localName = "ALIGN")
    private String align;

    /**
     * List of citations within the paragraph.
     */
    @JacksonXmlProperty(localName = "CITA")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CitationDTO> citations;

    /**
     * List of cross references within the paragraph.
     */
    @JacksonXmlProperty(localName = "CROSSREF")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CrossReferenceDTO> crossReferences;

    /**
     * List of footnotes within the paragraph.
     */
    @JacksonXmlProperty(localName = "FTNOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<FootnoteDTO> footnotes;

    /**
     * The source of the paragraph.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    @JacksonXmlProperty(localName = "TEXT")
    private String text;

    @JacksonXmlProperty(localName = "ELLIPSIS")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EllipsisDTO> ellipses;

    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    @JacksonXmlProperty(localName = "TABLE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableDTO> tables;

    @JacksonXmlProperty(localName = "GRAPHIC")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphicDTO> graphics;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AUTHORITY")
    private List<AuthorityDTO> authorities;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RESERVED")
    private List<ReservedDTO> reserved;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "EXTRACT")
    private List<ExtractDTO> extracts;
} 