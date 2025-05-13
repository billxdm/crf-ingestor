package com.ecfr.dto.ecfrxml;

import lombok.Data;

/**
 * DTO representing a footnote in the eCFR document.
 * Footnotes provide additional information or references.
 */
@Data
public class FootnoteDTO {
    private String number;
    private String content;
    private String reference;

    public String getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public String getReference() {
        return reference;
    }
} 