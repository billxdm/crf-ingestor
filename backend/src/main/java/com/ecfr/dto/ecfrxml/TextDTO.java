package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TextDTO {
    @JacksonXmlProperty(localName = "BODY")
    private BodyDTO body;

    @JacksonXmlProperty(localName = "TEXT")
    private String text;

    public BodyDTO getBody() {
        return body;
    }
} 