package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * DTO representing a table in the eCFR document.
 * Supports table attributes, footnotes, and captions.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TableDTO extends BaseDTO {
    /**
     * The table content.
     * Required field.
     */
    @NotBlank(message = "Table content is required")
    @JacksonXmlProperty(localName = "TABLE")
    private String content;

    /**
     * The table border width.
     */
    @Min(value = 0, message = "Border width cannot be negative")
    @JacksonXmlProperty(localName = "BORDER")
    private Integer border;

    /**
     * The table cell padding.
     */
    @Min(value = 0, message = "Cell padding cannot be negative")
    @JacksonXmlProperty(localName = "CELLPADDING")
    private Integer cellPadding;

    /**
     * The table cell spacing.
     */
    @Min(value = 0, message = "Cell spacing cannot be negative")
    @JacksonXmlProperty(localName = "CELLSPACING")
    private Integer cellSpacing;

    /**
     * The table width.
     */
    @JacksonXmlProperty(localName = "WIDTH")
    private String width;

    /**
     * The table alignment.
     */
    @JacksonXmlProperty(localName = "ALIGN")
    private String align;

    /**
     * The table caption.
     */
    @JacksonXmlProperty(localName = "CAPTION")
    private String caption;

    /**
     * List of table headers.
     */
    @JacksonXmlProperty(localName = "TH")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableHeaderDTO> headers;

    /**
     * List of table rows.
     */
    @JacksonXmlProperty(localName = "TR")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableRowDTO> rows;

    /**
     * List of table footnotes.
     */
    @JacksonXmlProperty(localName = "FTNOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<FootnoteDTO> footnotes;

    /**
     * The source of the table.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;
} 