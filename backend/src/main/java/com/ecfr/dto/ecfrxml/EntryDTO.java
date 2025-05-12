package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EntryDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "ENTRY")
    private String entry;
} 