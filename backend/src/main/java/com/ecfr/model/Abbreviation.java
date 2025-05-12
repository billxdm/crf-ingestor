package com.ecfr.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "abbreviations")
public class Abbreviation {
    private String id;
    private String titleNumber;
    private String chapterNumber;
    private String heading;
    private List<AbbreviationEntry> entries;
    private String nodeId;
    private String type = "ABBR";

    @Data
    public static class AbbreviationEntry {
        private String abbreviation;
        private String definition;
        private boolean italicized;
        public String getAbbreviation() { return abbreviation; }
        public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
        public String getDefinition() { return definition; }
        public void setDefinition(String definition) { this.definition = definition; }
        public boolean isItalicized() { return italicized; }
        public void setItalicized(boolean italicized) { this.italicized = italicized; }
    }

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setHeading(String heading) { this.heading = heading; }
    public void setEntries(List<AbbreviationEntry> entries) { this.entries = entries; }
} 