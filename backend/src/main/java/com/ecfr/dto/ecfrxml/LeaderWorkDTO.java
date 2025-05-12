package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO representing a leader work in the eCFR document.
 * Leader works are introductory or preliminary content.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaderWorkDTO extends BaseDTO {
    /**
     * The leader work content.
     * Required field.
     */
    @NotBlank(message = "Leader work content is required")
    @JacksonXmlProperty(localName = "LDRWK")
    private String content;

    /**
     * The title of the leader work.
     */
    @JacksonXmlProperty(localName = "TITLE")
    private String title;

    /**
     * List of paragraphs within the leader work.
     */
    @JacksonXmlProperty(localName = "P")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ParagraphDTO> paragraphs;

    /**
     * List of notes associated with the leader work.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;
} 