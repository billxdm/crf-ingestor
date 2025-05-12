package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
@Getter
@Setter
public class SectionDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

    @JacksonXmlProperty(localName = "P")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ParagraphDTO> paragraphs;

    @JacksonXmlProperty(localName = "APPENDIX")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AppendixDTO> appendices;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "NOTE")
    private List<NoteDTO> notes;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TABLE")
    private List<TableDTO> tables;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "GRAPHIC")
    private List<GraphicDTO> graphics;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AUTHORITY")
    private List<AuthorityDTO> authorities;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SOURCE")
    private List<SourceDTO> sources;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RESERVED")
    private List<ReservedDTO> reserved;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "EXTRACT")
    private List<ExtractDTO> extracts;
} 