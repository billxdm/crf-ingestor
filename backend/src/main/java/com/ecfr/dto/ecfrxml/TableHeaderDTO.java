package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a table header in the eCFR document.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TableHeaderDTO extends BaseDTO {
    /**
     * The header content.
     * Required field.
     */
    @NotBlank(message = "Header content is required")
    @JacksonXmlProperty(localName = "TH")
    private String content;

    /**
     * The header scope (row, col, rowgroup, colgroup).
     */
    @JacksonXmlProperty(localName = "SCOPE")
    private String scope;

    /**
     * The header alignment.
     */
    @JacksonXmlProperty(localName = "ALIGN")
    private String align;
} 