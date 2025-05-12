package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CellDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "ENT")
    private String content;

    @JacksonXmlProperty(localName = "COLNAME")
    private String columnName;

    @JacksonXmlProperty(localName = "COLSPAN")
    private Integer columnSpan;

    @JacksonXmlProperty(localName = "ROWSPAN")
    private Integer rowSpan;
} 