package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.List;

@Data
@Getter
@Setter
@Document(collection = "ecfr_documents")
@JacksonXmlRootElement(localName = "ECFR")
public class EcfrDTO {
    @Id
    private String id;

    @Indexed
    @JacksonXmlProperty(localName = "HEADER")
    private HeaderDTO header;

    @JacksonXmlProperty(localName = "TEXT")
    private TextDTO text;

    @JacksonXmlProperty(localName = "DIV1")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> divisions;

    @Indexed
    private String titleNumber;

    public String getId() {
        if (text != null && text.getBody() != null && text.getBody().getEcfrbrws() != null) {
            return text.getBody().getEcfrbrws().getId();
        }
        return id;
    }

    public String getTitleNumber() {
        if (titleNumber == null && text != null && text.getBody() != null && text.getBody().getEcfrbrws() != null) {
            titleNumber = text.getBody().getEcfrbrws().getTitle();
        }
        return titleNumber;
    }

    public void setTitleNumber(String titleNumber) {
        this.titleNumber = titleNumber;
    }

    public HeaderDTO getHeader() {
        return header;
    }

    public TextDTO getText() {
        return text;
    }
} 