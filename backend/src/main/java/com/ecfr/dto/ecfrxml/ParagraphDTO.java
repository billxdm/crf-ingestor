package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO representing a paragraph in the eCFR document.
 * Supports different paragraph types and formatting.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParagraphDTO extends BaseDTO {
    /**
     * The paragraph content.
     * Required field.
     */
    @NotBlank(message = "Paragraph content is required")
    @JacksonXmlText
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

    @JacksonXmlProperty(localName = "NODEID")
    private String nodeId;

    @JacksonXmlProperty(localName = "NUMBER")
    private String number;

    @JacksonXmlProperty(localName = "INDENTATION_LEVEL")
    private int indentationLevel;

    @JacksonXmlProperty(localName = "CLASSES")
    private List<String> classes;

    @JacksonXmlProperty(localName = "ATTRIBUTES")
    private Map<String, String> attributes;

    @JacksonXmlProperty(localName = "IS_BOLD")
    private boolean isBold;

    @JacksonXmlProperty(localName = "IS_ITALIC")
    private boolean isItalic;

    @JacksonXmlProperty(localName = "IS_UNDERLINED")
    private boolean isUnderlined;

    public ParagraphDTO() {
        // Default constructor for Jackson
    }

    public ParagraphDTO(String text) {
        this.text = text;
    }

    // Additional getter/setter methods
    public boolean isBold() {
        return isBold;
    }

    public void setIsBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setIsItalic(boolean isItalic) {
        this.isItalic = isItalic;
    }

    public boolean isUnderlined() {
        return isUnderlined;
    }

    public void setIsUnderlined(boolean isUnderlined) {
        this.isUnderlined = isUnderlined;
    }

    public int getIndentationLevel() {
        return indentationLevel;
    }

    public void setIndentationLevel(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getContent() {
        return content;
    }

    public List<CitationDTO> getCitations() {
        return citations;
    }

    public List<GraphicDTO> getGraphics() {
        return graphics;
    }

    public List<FootnoteDTO> getFootnotes() {
        return footnotes;
    }

    public String getNumber() {
        return number;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getText() {
        return text;
    }
} 