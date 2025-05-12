package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO representing an authority section in the eCFR document.
 * Authority sections provide legal citations and references.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityDTO extends BaseDTO {
    /**
     * The heading of the authority section.
     */
    @JacksonXmlProperty(localName = "HED")
    private String heading;

    /**
     * The paragraph space content.
     */
    @JacksonXmlProperty(localName = "PSPACE")
    private String paragraphSpace;

    /**
     * The source of the authority.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    /**
     * The authority content.
     * Required field.
     */
    @NotBlank(message = "Authority content is required")
    @JacksonXmlProperty(localName = "AUTHORITY_CONTENT")
    private String content;

    /**
     * The type of authority.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The level of the authority in the hierarchy.
     */
    @JacksonXmlProperty(localName = "LEVEL")
    @Field("authorityLevel")
    private String level;

    /**
     * List of sub-authorities.
     */
    @JacksonXmlProperty(localName = "AUTHORITY")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AuthorityDTO> subAuthorities;

    /**
     * List of citations within the authority.
     */
    @JacksonXmlProperty(localName = "CITA")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CitationDTO> citations;

    /**
     * List of cross references within the authority.
     */
    @JacksonXmlProperty(localName = "CROSSREF")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CrossReferenceDTO> crossReferences;

    /**
     * List of notes within the authority.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;
} 