package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import java.util.List;

@Data
public class BodyDTO {
    @JacksonXmlProperty(localName = "ECFRBRWS")
    private EcfrbrwsDTO ecfrbrws;

    @JacksonXmlProperty(localName = "DIV1")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> divisions;

    @JacksonXmlProperty(localName = "PART")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PartDTO> parts;

    @JacksonXmlProperty(localName = "SUBJECT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SubjectDTO> subjects;

    public EcfrbrwsDTO getEcfrbrws() {
        return ecfrbrws;
    }
} 