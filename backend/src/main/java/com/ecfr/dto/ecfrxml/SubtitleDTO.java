package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
@Getter
@Setter
public class SubtitleDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

    @JacksonXmlProperty(localName = "DIV3")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ChapterDTO> chapters;

    @JacksonXmlProperty(localName = "DIV4")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubchapterDTO> subchapters;

    @JacksonXmlProperty(localName = "DIV5")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PartDTO> parts;

    @JacksonXmlProperty(localName = "DIV6")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubpartDTO> subparts;

    @JacksonXmlProperty(localName = "DIV8")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SectionDTO> sections;

    @JacksonXmlProperty(localName = "APPENDIX")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AppendixDTO> appendices;
} 