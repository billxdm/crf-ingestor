package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * DTO representing the eCFR browse information structure.
 * This class contains metadata about the document's location in the eCFR hierarchy
 * and its effective dates.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcfrbrwsDTO extends BaseDTO {
    /**
     * Title of the document.
     * Required field.
     */
    @NotBlank(message = "Title is required")
    @JacksonXmlProperty(localName = "TITLE")
    private String title;

    /**
     * Subtitle of the document.
     * Required field.
     */
    @NotBlank(message = "Subtitle is required")
    @JacksonXmlProperty(localName = "SUBTITLE")
    private String subtitle;

    /**
     * Chapter identifier.
     * Required field. Must contain only letters, numbers, hyphens, and underscores.
     */
    @NotBlank(message = "Chapter is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Chapter must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "CHAPTER")
    private String chapter;

    /**
     * Subchapter identifier.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Subchapter must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "SUBCHAPTER")
    private String subchapter;

    /**
     * Part identifier.
     * Required field. Must contain only letters, numbers, hyphens, and underscores.
     */
    @NotBlank(message = "Part is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Part must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "PART")
    private String part;

    /**
     * Subject identifier.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Subject must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "SUBJECT")
    private String subject;

    /**
     * Section identifier.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Section must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "SECTION")
    private String section;

    /**
     * Effective date of the document in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Effective date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "EFFDATE")
    private String effectiveDate;

    @JacksonXmlProperty(localName = "DIV1")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DivisionDTO> divisions;

    public List<DivisionDTO> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<DivisionDTO> divisions) {
        this.divisions = divisions;
    }

    public String getId() {
        return super.getId();
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getChapter() {
        return chapter;
    }

    public String getSubchapter() {
        return subchapter;
    }

    public String getPart() {
        return part;
    }

    public String getSection() {
        return section;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
} 