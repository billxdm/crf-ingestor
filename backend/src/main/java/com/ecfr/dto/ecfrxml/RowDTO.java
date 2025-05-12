package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RowDTO extends BaseDTO {
    @JacksonXmlProperty(localName = "ENT")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CellDTO> cells;

    @JacksonXmlProperty(localName = "NOTE")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TableNoteDTO> notes;
} 