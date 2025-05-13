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
    @JacksonXmlProperty(localName = "N", isAttribute = true)
    private String n;

    /**
     * The division type (e.g., "TITLE", "SUBTITLE", "PART", "SECTION").
     */
    @JacksonXmlProperty(localName = "TYPE", isAttribute = true)
    private String type;

    /**
     * The division volume.
     */
    @JacksonXmlProperty(localName = "VOLUME", isAttribute = true)
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

    @JacksonXmlProperty(localName = "SOURCE")
    private SourceDTO source;

    @JacksonXmlProperty(localName = "CITA")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CitationDTO> citations;

    @JacksonXmlProperty(localName = "CROSSREF")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CrossReferenceDTO> crossReferences;

    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;

    @JacksonXmlProperty(localName = "EFFDNOT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EffectiveDateNoteDTO> effectiveDateNotes;

    @JacksonXmlProperty(localName = "EDNOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EditorialNoteDTO> editorialNotes;

    @JacksonXmlProperty(localName = "EXTRACT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ExtractDTO> extracts;

    @JacksonXmlProperty(localName = "GRAPHIC")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphicDTO> graphics;

    @JacksonXmlProperty(localName = "RESERVED")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ReservedDTO> reserved;

    @JacksonXmlProperty(localName = "TCAP")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TopCaptionDTO> topCaptions;

    @JacksonXmlProperty(localName = "ENTRY")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EntryDTO> entries;

    @JacksonXmlProperty(localName = "SUBJECT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubjectDTO> subjects;

    @JacksonXmlProperty(localName = "PART")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PartDTO> parts;

    @JacksonXmlProperty(localName = "DIV1")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div1;

    @JacksonXmlProperty(localName = "DIV2")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div2;

    @JacksonXmlProperty(localName = "DIV3")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div3;

    @JacksonXmlProperty(localName = "DIV4")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div4;

    @JacksonXmlProperty(localName = "DIV5")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div5;

    @JacksonXmlProperty(localName = "DIV6")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div6;

    @JacksonXmlProperty(localName = "DIV7")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div7;

    @JacksonXmlProperty(localName = "DIV8")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div8;

    @JacksonXmlProperty(localName = "DIV9")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> div9;

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

    @JacksonXmlProperty(localName = "APPENDIX")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AppendixDTO> appendices;

    @JacksonXmlProperty(localName = "NODEID")
    private String nodeId;

    public List<ParagraphDTO> getParagraphs() {
        return paragraphs;
    }
    public List<TableDTO> getTables() {
        return tables;
    }
    public List<GraphicDTO> getGraphics() {
        return graphics;
    }
    public List<ExtractDTO> getExtracts() {
        return extracts;
    }
    public List<DivisionDTO> getDiv1() {
        return div1;
    }
    public List<DivisionDTO> getDiv2() {
        return div2;
    }
    public List<DivisionDTO> getDiv3() {
        return div3;
    }
    public List<DivisionDTO> getDiv4() {
        return div4;
    }
    public List<DivisionDTO> getDiv5() {
        return div5;
    }
    public List<DivisionDTO> getDiv6() {
        return div6;
    }
    public List<DivisionDTO> getDiv7() {
        return div7;
    }
    public List<DivisionDTO> getDiv8() {
        return div8;
    }
    public List<DivisionDTO> getDiv9() {
        return div9;
    }
    public String getId() {
        return super.getId();
    }
    public String getHead() {
        return head;
    }
    public String getType() {
        return type;
    }
    public String getNumber() {
        return n;
    }
    public String getVolume() {
        return volume;
    }

    public AuthorityDTO getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityDTO authority) {
        this.authority = authority;
    }

    public SourceDTO getSource() {
        return source;
    }

    public void setSource(SourceDTO source) {
        this.source = source;
    }

    public List<CitationDTO> getCitations() {
        return citations;
    }

    public void setCitations(List<CitationDTO> citations) {
        this.citations = citations;
    }

    public List<CrossReferenceDTO> getCrossReferences() {
        return crossReferences;
    }

    public void setCrossReferences(List<CrossReferenceDTO> crossReferences) {
        this.crossReferences = crossReferences;
    }

    public List<NoteDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteDTO> notes) {
        this.notes = notes;
    }

    public List<EffectiveDateNoteDTO> getEffectiveDateNotes() {
        return effectiveDateNotes;
    }

    public void setEffectiveDateNotes(List<EffectiveDateNoteDTO> effectiveDateNotes) {
        this.effectiveDateNotes = effectiveDateNotes;
    }

    public List<EditorialNoteDTO> getEditorialNotes() {
        return editorialNotes;
    }

    public void setEditorialNotes(List<EditorialNoteDTO> editorialNotes) {
        this.editorialNotes = editorialNotes;
    }

    public List<ReservedDTO> getReserved() {
        return reserved;
    }

    public void setReserved(List<ReservedDTO> reserved) {
        this.reserved = reserved;
    }

    public List<TopCaptionDTO> getTopCaptions() {
        return topCaptions;
    }

    public void setTopCaptions(List<TopCaptionDTO> topCaptions) {
        this.topCaptions = topCaptions;
    }

    public List<EntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryDTO> entries) {
        this.entries = entries;
    }

    public List<SubjectDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDTO> subjects) {
        this.subjects = subjects;
    }

    public List<PartDTO> getParts() {
        return parts;
    }

    public void setParts(List<PartDTO> parts) {
        this.parts = parts;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumber(String n) {
        this.n = n;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setParagraphs(List<ParagraphDTO> paragraphs) {
        this.paragraphs = paragraphs;
    }
} 