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
public class ChapterDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

    @JacksonXmlProperty(localName = "DIV4")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubchapterDTO> subchapters;

    @Override
    public String getType() {
        return "CHAPTER";
    }
} 