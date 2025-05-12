package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * DTO representing a division in the eCFR document.
 * Supports hierarchical structure from DIV1 through DIV9.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DivisionDTO extends BaseDTO {
    /**
     * The division number (e.g., "43" for Title 43).
     */
    @JacksonXmlProperty(localName = "N")
    private String number;

    /**
     * The division type (e.g., "TITLE", "SUBTITLE", "PART", "SECTION").
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The division volume.
     */
    @JacksonXmlProperty(localName = "VOLUME")
    private String volume;

    /**
     * The division heading.
     */
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

    /**
     * The authority section.
     */
    @JacksonXmlProperty(localName = "AUTH")
    private AuthorityDTO authority;

    /**
     * List of sub-divisions.
     */
    @JacksonXmlProperty(localName = "DIV2")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> subDivisions;

    /**
     * List of paragraphs within the division.
     */
    @JacksonXmlProperty(localName = "P")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ParagraphDTO> paragraphs;

    /**
     * List of tables within the division.
     */
    @JacksonXmlProperty(localName = "TABLE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableDTO> tables;

    /**
     * List of notes within the division.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    @JacksonXmlProperty(localName = "APPENDIX")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AppendixDTO> appendices;

    @JacksonXmlProperty(localName = "GRAPHIC")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphicDTO> graphics;

    @JacksonXmlProperty(localName = "AUTHORITY")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AuthorityDTO> authorities;

    @JacksonXmlProperty(localName = "RESERVED")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ReservedDTO> reserved;

    @JacksonXmlProperty(localName = "EXTRACT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ExtractDTO> extracts;

    public List<ParagraphDTO> getParagraphs() {
        return paragraphs;
    }
    public List<TableDTO> getTables() {
        return tables;
    }
    public List<GraphicDTO> getGraphics() {
        return graphics;
    }
    public List<AuthorityDTO> getAuthorities() {
        return authorities;
    }
    public List<ExtractDTO> getExtracts() {
        return extracts;
    }
    public List<DivisionDTO> getSubDivisions() {
        return subDivisions;
    }
    public String getId() {
        return super.getId();
    }
    public String getHead() {
        return head;
    }
} 