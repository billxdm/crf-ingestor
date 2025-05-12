package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing a graphic element in the eCFR document.
 * Supports both image and PDF content.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GraphicDTO extends BaseDTO {
    /**
     * The graphic content.
     * Required field.
     */
    @NotBlank(message = "Graphic content is required")
    @JacksonXmlProperty(localName = "GRAPHIC")
    private String content;

    /**
     * The type of graphic (image, pdf).
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The source URL of the graphic.
     * Required for external graphics.
     */
    @Pattern(regexp = "^https?://.*", message = "Source URL must be a valid HTTP(S) URL")
    @JacksonXmlProperty(localName = "SOURCE")
    private String sourceUrl;

    /**
     * The PDF link URL.
     * Required for PDF graphics.
     */
    @Pattern(regexp = "^https?://.*\\.pdf$", message = "PDF URL must be a valid PDF file URL")
    @JacksonXmlProperty(localName = "PDF")
    private String pdfUrl;

    /**
     * The width of the graphic.
     */
    @JacksonXmlProperty(localName = "WIDTH")
    private String width;

    /**
     * The height of the graphic.
     */
    @JacksonXmlProperty(localName = "HEIGHT")
    private String height;

    /**
     * The alt text for the graphic.
     */
    @JacksonXmlProperty(localName = "ALT")
    private String altText;
} 