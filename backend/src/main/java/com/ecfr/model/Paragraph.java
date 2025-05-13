package com.ecfr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "paragraphs")
public class Paragraph {
    @Id
    private String id;
    
    // Basic Information
    private String nodeId;
    private String type = "PARAGRAPH";
    private String number;  // e.g., "(a)", "(1)", etc.
    
    // Content
    private String content;
    private String formattedContent;  // HTML formatted content
    
    // Formatting
    private int indentationLevel;
    private List<String> classes;  // CSS classes for styling
    private Map<String, String> attributes;  // Additional XML attributes
    
    // References
    private String sectionId;  // Reference to parent section
    private String titleNumber;
    private String partNumber;
    
    // Nested Elements
    private List<Citation> citations;
    private List<Table> tables;
    private List<Graphic> graphics;
    private List<Footnote> footnotes;
    
    // Metadata
    private int wordCount;
    private boolean isIndented;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderlined;
    
    @Data
    public static class Citation {
        private String type;  // e.g., "AUTHORITY", "SOURCE", "CROSSREF"
        private String content;
        private String reference;
        private String date;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReference() {
            return this.reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getDate() {
            return this.date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
    
    @Data
    public static class Footnote {
        private String number;
        private String content;
        private String reference;

        public String getNumber() {
            return this.number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReference() {
            return this.reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
    
    @Data
    public static class Graphic {
        private String type;  // e.g., "IMAGE", "FIGURE", "TABLE"
        private String src;
        private String alt;
        private String caption;
        private Map<String, String> attributes;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSrc() {
            return this.src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getAlt() {
            return this.alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getCaption() {
            return this.caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }
    }
    
    @Data
    public static class Table {
        private String id;
        private String caption;
        private List<String> headers;
        private List<List<String>> rows;
        private Map<String, String> attributes;
    }

    // Additional getter/setter methods
    public boolean isIndented() {
        return isIndented;
    }

    public void setIsIndented(boolean isIndented) {
        this.isIndented = isIndented;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setIsBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setIsItalic(boolean isItalic) {
        this.isItalic = isItalic;
    }

    public boolean isUnderlined() {
        return isUnderlined;
    }

    public void setIsUnderlined(boolean isUnderlined) {
        this.isUnderlined = isUnderlined;
    }

    public void setTitleNumber(String titleNumber) {
        this.titleNumber = titleNumber;
    }

    public void setCitations(List<Citation> citations) {
        this.citations = citations;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public void setFootnotes(List<Footnote> footnotes) {
        this.footnotes = footnotes;
    }

    public String getNumber() {
        return this.number;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setIndentationLevel(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public void setGraphics(List<Graphic> graphics) {
        this.graphics = graphics;
    }

    public String getContent() {
        return this.content;
    }

    public int getIndentationLevel() {
        return this.indentationLevel;
    }

    public List<Citation> getCitations() {
        return this.citations;
    }

    public List<Graphic> getGraphics() {
        return this.graphics;
    }

    public List<Footnote> getFootnotes() {
        return this.footnotes;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNumber(String number) {
        this.number = number;
    }
} 