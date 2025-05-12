package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Base DTO class that contains common fields for all XML elements in the eCFR system.
 * This class provides the fundamental structure and validation rules for all derived DTOs.
 */
@Data
@EqualsAndHashCode
public class BaseDTO {
    /**
     * Unique identifier for the element.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @NotBlank(message = "ID is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "ID must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "ID")
    private String id;

    /**
     * Type of the element.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Type must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "TYPE")
    @Field("baseType")
    private String type;

    /**
     * Numeric or alphanumeric identifier for the element.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "N must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "N")
    private String n;

    /**
     * Level of the element in the document hierarchy.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Level must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "LEVEL")
    @Field("baseLevel")
    private String level;

    /**
     * Label for the element.
     * Must contain only letters, numbers, hyphens, and underscores.
     */
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Label must contain only letters, numbers, hyphens, and underscores")
    @JacksonXmlProperty(localName = "LABEL")
    private String label;

    /**
     * Title of the element.
     */
    @JacksonXmlProperty(localName = "TITLE")
    @Field("baseTitle")
    private String title;

    /**
     * Effective date of the element in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Effective date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "EFFDATE")
    @Field("baseEffectiveDate")
    private String effectiveDate;

    /**
     * Amendment date of the element in YYYY-MM-DD format.
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Amendment date must be in YYYY-MM-DD format")
    @JacksonXmlProperty(localName = "AMDDATE")
    @Field("baseAmendmentDate")
    private String amendmentDate;

    /**
     * The node type of the element.
     * Required field.
     */
    @NotBlank(message = "Node type is required")
    @JacksonXmlProperty(localName = "NODE")
    private String nodeType;

    public String getId() {
        return id;
    }
} 