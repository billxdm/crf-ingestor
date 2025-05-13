package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Document(collection = "ecfr_documents")
@JacksonXmlRootElement(localName = "ECFR")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcfrDTO {
    @Id
    private String id;

    @JacksonXmlProperty(localName = "AMDDATE")
    private String amendmentDate;

    @JacksonXmlProperty(localName = "VOLUME")
    private VolumeDTO volume;

    @JacksonXmlProperty(localName = "DIV1")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> divisions;

    @Indexed
    private String titleNumber;

    public String getId() {
        if (divisions != null && !divisions.isEmpty()) {
            DivisionDTO firstDivision = divisions.get(0);
            if (firstDivision != null && "TITLE".equals(firstDivision.getType())) {
                return firstDivision.getN();
            }
        }
        return id;
    }

    public String getTitleNumber() {
        if (titleNumber == null && divisions != null && !divisions.isEmpty()) {
            for (DivisionDTO division : divisions) {
                if (division != null && "TITLE".equals(division.getType())) {
                    titleNumber = division.getN();
                    break;
                }
            }
        }
        return titleNumber;
    }
} 