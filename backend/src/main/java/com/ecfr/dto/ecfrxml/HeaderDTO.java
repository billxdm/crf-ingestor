package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DTO class representing the header section of an eCFR document.
 * Contains metadata about the document such as title, effective date, and source.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderDTO extends BaseDTO {
    /**
     * Title of the document.
     * Required field.
     */
    @NotBlank(message = "Title is required")
    @JacksonXmlProperty(localName = "TITLE")
    @Field("headerTitle")
    private String title;

    /**
     * Effective date of the document in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Effective date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "EFFDATE")
    @Field("headerEffectiveDate")
    private String effectiveDate;

    /**
     * Source of the document.
     */
    @JacksonXmlProperty(localName = "SOURCE")
    private String source;

    public String getTitle() {
        return title;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTitle(String title) {
        this.title = title;
    }
} 