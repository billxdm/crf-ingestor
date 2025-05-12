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
public class SubchapterDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

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