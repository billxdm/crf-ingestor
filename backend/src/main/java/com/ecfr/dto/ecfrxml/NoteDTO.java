package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NoteDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "NOTE")
    private String note;
} 