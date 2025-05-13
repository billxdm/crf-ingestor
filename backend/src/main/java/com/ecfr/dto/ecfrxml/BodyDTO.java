package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

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

    public void setEcfrbrws(EcfrbrwsDTO ecfrbrws) {
        this.ecfrbrws = ecfrbrws;
    }

    public List<DivisionDTO> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<DivisionDTO> divisions) {
        this.divisions = divisions;
    }

    public DivisionDTO getRootDivision() {
        return divisions != null && !divisions.isEmpty() ? divisions.get(0) : null;
    }

    public void setRootDivision(DivisionDTO rootDivision) {
        if (divisions == null) {
            divisions = new ArrayList<>();
        }
        if (!divisions.isEmpty()) {
            divisions.set(0, rootDivision);
        } else {
            divisions.add(rootDivision);
        }
    }
} 