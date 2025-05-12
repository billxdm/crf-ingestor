package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReservedDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "RESERVED")
    private String reserved;
} 