package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing mathematical content in the eCFR document.
 * This class handles mathematical expressions and formulas.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MathDTO extends BaseDTO {
    /**
     * The mathematical content.
     * Required field.
     */
    @NotBlank(message = "Mathematical content is required")
    @JacksonXmlProperty(localName = "MATH")
    private String content;

    /**
     * The type of mathematical expression.
     */
    @JacksonXmlProperty(localName = "TYPE")
    private String type;

    /**
     * The format of the mathematical expression.
     */
    @JacksonXmlProperty(localName = "FORMAT")
    private String format;

    /**
     * The source of the mathematical expression.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 