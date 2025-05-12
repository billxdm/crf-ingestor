package com.ecfr.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "indexes")
public class Index {
    private String id;
    private String titleNumber;
    private String partNumber;
    private String nodeId;
    private String type = "SUBCHIND";
    
    private String heading;
    private String editorialNote;
    private List<IndexSection> sections;
    
    @Data
    public static class IndexSection {
        private String sectionHeader;
        private String alphaHeader;
        private List<IndexEntry> entries;
        public String getSectionHeader() { return sectionHeader; }
        public void setSectionHeader(String sectionHeader) { this.sectionHeader = sectionHeader; }
        public String getAlphaHeader() { return alphaHeader; }
        public void setAlphaHeader(String alphaHeader) { this.alphaHeader = alphaHeader; }
        public List<IndexEntry> getEntries() { return entries; }
        public void setEntries(List<IndexEntry> entries) { this.entries = entries; }
    }
    
    @Data
    public static class IndexEntry {
        private String subject;
        private List<String> subSubjects;
        private List<String> partReferences;
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public List<String> getSubSubjects() { return subSubjects; }
        public void setSubSubjects(List<String> subSubjects) { this.subSubjects = subSubjects; }
        public List<String> getPartReferences() { return partReferences; }
        public void setPartReferences(List<String> partReferences) { this.partReferences = partReferences; }
    }

    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setHeading(String heading) { this.heading = heading; }
    public void setEditorialNote(String editorialNote) { this.editorialNote = editorialNote; }
    public void setSections(List<IndexSection> sections) { this.sections = sections; }
} 