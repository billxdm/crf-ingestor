package com.ecfr.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "tables")
public class Table {
    private String id;
    private String titleNumber;
    private String partNumber;
    private String sectionNumber;
    private String nodeId;
    private String type = "TABLE";
    
    // Table attributes
    private String border;
    private String cellPadding;
    private String cellSpacing;
    private String frame;
    private String width;
    private String className;
    
    // Table content
    private List<String> columnHeaders;
    private List<TableRow> rows;
    private List<TableFootnote> footnotes;
    
    @Data
    public static class TableRow {
        private List<TableCell> cells;
        private Map<String, String> attributes; // For scope, align, etc.
        public List<TableCell> getCells() { return cells; }
        public void setCells(List<TableCell> cells) { this.cells = cells; }
        public Map<String, String> getAttributes() { return attributes; }
        public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    }
    
    @Data
    public static class TableCell {
        private String content;
        private boolean isHeader;
        private String scope;
        private String alignment;
        private String className;
        private List<String> footnotes; // For superscript references
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public boolean isHeader() { return isHeader; }
        public void setHeader(boolean isHeader) { this.isHeader = isHeader; }
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        public String getAlignment() { return alignment; }
        public void setAlignment(String alignment) { this.alignment = alignment; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public List<String> getFootnotes() { return footnotes; }
        public void setFootnotes(List<String> footnotes) { this.footnotes = footnotes; }
    }
    
    @Data
    public static class TableFootnote {
        private String number;
        private String content;
        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setBorder(String border) { this.border = border; }
    public void setCellPadding(String cellPadding) { this.cellPadding = cellPadding; }
    public void setCellSpacing(String cellSpacing) { this.cellSpacing = cellSpacing; }
    public void setFrame(String frame) { this.frame = frame; }
    public void setWidth(String width) { this.width = width; }
    public void setClassName(String className) { this.className = className; }
    public void setColumnHeaders(List<String> columnHeaders) { this.columnHeaders = columnHeaders; }
    public void setRows(List<TableRow> rows) { this.rows = rows; }
    public void setFootnotes(List<TableFootnote> footnotes) { this.footnotes = footnotes; }
} 