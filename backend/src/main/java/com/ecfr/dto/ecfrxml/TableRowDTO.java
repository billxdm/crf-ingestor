package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO representing a table row in the eCFR document.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TableRowDTO extends BaseDTO {
    /**
     * List of cells in the row.
     * Required field. Must not be empty.
     */
    @NotEmpty(message = "Row must contain at least one cell")
    @JacksonXmlProperty(localName = "TD")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CellDTO> cells;

    /**
     * The row alignment.
     */
    @JacksonXmlProperty(localName = "ALIGN")
    private String align;

    /**
     * The row vertical alignment.
     */
    @JacksonXmlProperty(localName = "VALIGN")
    private String verticalAlign;
} 