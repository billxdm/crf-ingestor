package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EllipsisDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "ELLIPSIS")
    private String ellipsis;
} 