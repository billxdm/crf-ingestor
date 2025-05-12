package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO representing an example in the eCFR document.
 * Examples provide illustrative content to clarify regulations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExampleDTO extends BaseDTO {
    /**
     * The example content.
     * Required field.
     */
    @NotBlank(message = "Example content is required")
    @JacksonXmlProperty(localName = "EXAMPLE")
    private String content;

    /**
     * The title of the example.
     */
    @JacksonXmlProperty(localName = "TITLE")
    private String title;

    /**
     * List of paragraphs within the example.
     */
    @JacksonXmlProperty(localName = "P")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ParagraphDTO> paragraphs;

    /**
     * List of notes associated with the example.
     */
    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NoteDTO> notes;
} 