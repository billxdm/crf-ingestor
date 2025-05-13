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
public class TitleDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "HEAD")
    private String head;

    @JacksonXmlProperty(localName = "DIV3")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ChapterDTO> chapters;

    public String getTitle() {
        return head;
    }

    @Override
    public String getType() {
        return "TITLE";
    }

    public String getCHAPTER() {
        return null;
    }

    public String getPART() {
        return null;
    }

    public String getSECTION() {
        return null;
    }
} 