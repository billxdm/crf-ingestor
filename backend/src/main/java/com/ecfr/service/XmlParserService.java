package com.ecfr.service;

import com.ecfr.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XmlParserService {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserService.class);
    private static final Pattern PARAGRAPH_NUMBER_PATTERN = Pattern.compile("^\\(([a-z0-9]+)\\)\\s*(.*)");
    private static final DateTimeFormatter CITATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public CfrSection parseSectionXml(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            
            Element root = document.getDocumentElement();
            return parseSectionElement(root);
        } catch (Exception e) {
            logger.error("Error parsing XML content", e);
            throw new RuntimeException("Failed to parse XML content", e);
        }
    }

    private CfrSection parseSectionElement(Element element) {
        CfrSection section = new CfrSection();
        
        // Parse basic section information
        section.setNodeId(element.getAttribute("NODE"));
        section.setSectionNumber(element.getAttribute("N"));
        
        // Parse HEAD element for section name
        NodeList headNodes = element.getElementsByTagName("HEAD");
        if (headNodes.getLength() > 0) {
            String headText = headNodes.item(0).getTextContent().trim();
            section.setName(headText);
        }
        
        // Parse paragraphs
        List<CfrSection.Paragraph> paragraphs = new ArrayList<>();
        NodeList pNodes = element.getElementsByTagName("P");
        for (int i = 0; i < pNodes.getLength(); i++) {
            Element pElement = (Element) pNodes.item(i);
            CfrSection.Paragraph paragraph = parseParagraph(pElement);
            if (paragraph != null) {
                paragraphs.add(paragraph);
            }
        }
        section.setParagraphs(paragraphs);
        
        // Parse citations
        List<CfrSection.Citation> citations = new ArrayList<>();
        NodeList citaNodes = element.getElementsByTagName("CITA");
        for (int i = 0; i < citaNodes.getLength(); i++) {
            Element citaElement = (Element) citaNodes.item(i);
            CfrSection.Citation citation = parseCitation(citaElement);
            if (citation != null) {
                citations.add(citation);
            }
        }
        section.setCitations(citations);
        
        // Parse tables
        List<Table> tables = new ArrayList<>();
        NodeList tableNodes = element.getElementsByTagName("TABLE");
        for (int i = 0; i < tableNodes.getLength(); i++) {
            Element tableElement = (Element) tableNodes.item(i);
            Table table = parseTable(tableElement);
            if (table != null) {
                tables.add(table);
            }
        }
        section.setTables(tables);
        
        // Parse abbreviations
        List<Abbreviation> abbreviations = new ArrayList<>();
        NodeList abbrNodes = element.getElementsByTagName("ABBR");
        for (int i = 0; i < abbrNodes.getLength(); i++) {
            Element abbrElement = (Element) abbrNodes.item(i);
            Abbreviation abbreviation = parseAbbreviation(abbrElement);
            if (abbreviation != null) {
                abbreviations.add(abbreviation);
            }
        }
        section.setAbbreviations(abbreviations);
        
        // Parse indexes
        List<Index> indexes = new ArrayList<>();
        NodeList indexNodes = element.getElementsByTagName("SUBCHIND");
        for (int i = 0; i < indexNodes.getLength(); i++) {
            Element indexElement = (Element) indexNodes.item(i);
            Index index = parseIndex(indexElement);
            if (index != null) {
                indexes.add(index);
            }
        }
        section.setIndexes(indexes);
        
        return section;
    }

    private CfrSection.Paragraph parseParagraph(Element pElement) {
        CfrSection.Paragraph paragraph = new CfrSection.Paragraph();
        paragraph.setType(pElement.getTagName());
        
        // Get paragraph content and check for numbering
        String content = pElement.getTextContent().trim();
        Matcher matcher = PARAGRAPH_NUMBER_PATTERN.matcher(content);
        
        if (matcher.matches()) {
            paragraph.setNumber(matcher.group(1));
            content = matcher.group(2).trim();
        }
        
        // Check for italicized text
        NodeList iNodes = pElement.getElementsByTagName("I");
        if (iNodes.getLength() > 0) {
            paragraph.setItalicized(true);
        }
        
        paragraph.setContent(content);
        
        // Check for sub-paragraphs
        List<CfrSection.Paragraph> subParagraphs = new ArrayList<>();
        NodeList childNodes = pElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element && isParagraphElement((Element) child)) {
                CfrSection.Paragraph subParagraph = parseParagraph((Element) child);
                if (subParagraph != null) {
                    subParagraphs.add(subParagraph);
                }
            }
        }
        paragraph.setSubParagraphs(subParagraphs);
        
        return paragraph;
    }

    private CfrSection.Citation parseCitation(Element citaElement) {
        CfrSection.Citation citation = new CfrSection.Citation();
        citation.setType(citaElement.getAttribute("TYPE"));
        citation.setContent(citaElement.getTextContent().trim());
        
        // Parse citation content to extract dates and document numbers
        String content = citation.getContent();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
            
            // Extract document numbers
            Pattern docPattern = Pattern.compile("(\\d+)\\s+FR\\s+(\\d+)");
            Matcher docMatcher = docPattern.matcher(content);
            if (docMatcher.find()) {
                citation.setDocumentNumber(docMatcher.group(1) + " FR " + docMatcher.group(2));
            }
            
            // Extract dates
            Pattern datePattern = Pattern.compile("([A-Za-z]+\\.?\\s+\\d{1,2},\\s+\\d{4})");
            Matcher dateMatcher = datePattern.matcher(content);
            if (dateMatcher.find()) {
                try {
                    LocalDateTime date = LocalDateTime.parse(dateMatcher.group(1), CITATION_DATE_FORMATTER);
                    citation.setEffectiveDate(date);
                } catch (Exception e) {
                    logger.warn("Failed to parse citation date: {}", dateMatcher.group(1));
                }
            }
        }
        
        return citation;
    }

    private Table parseTable(Element tableElement) {
        Table table = new Table();
        table.setNodeId(tableElement.getAttribute("NODE"));
        
        // Parse table attributes
        table.setBorder(tableElement.getAttribute("border"));
        table.setCellPadding(tableElement.getAttribute("cellpadding"));
        table.setCellSpacing(tableElement.getAttribute("cellspacing"));
        table.setFrame(tableElement.getAttribute("frame"));
        table.setWidth(tableElement.getAttribute("width"));
        table.setClassName(tableElement.getAttribute("class"));
        
        // Parse table content
        List<String> headers = new ArrayList<>();
        List<Table.TableRow> rows = new ArrayList<>();
        List<Table.TableFootnote> footnotes = new ArrayList<>();
        
        NodeList trNodes = tableElement.getElementsByTagName("TR");
        for (int i = 0; i < trNodes.getLength(); i++) {
            Element trElement = (Element) trNodes.item(i);
            Table.TableRow row = new Table.TableRow();
            List<Table.TableCell> cells = new ArrayList<>();
            
            NodeList tdNodes = trElement.getElementsByTagName("TD");
            NodeList thNodes = trElement.getElementsByTagName("TH");
            
            // Process header cells
            for (int j = 0; j < thNodes.getLength(); j++) {
                Element thElement = (Element) thNodes.item(j);
                Table.TableCell cell = parseTableCell(thElement, true);
                cells.add(cell);
                headers.add(cell.getContent());
            }
            
            // Process data cells
            for (int j = 0; j < tdNodes.getLength(); j++) {
                Element tdElement = (Element) tdNodes.item(j);
                Table.TableCell cell = parseTableCell(tdElement, false);
                cells.add(cell);
            }
            
            row.setCells(cells);
            rows.add(row);
        }
        
        table.setColumnHeaders(headers);
        table.setRows(rows);
        table.setFootnotes(footnotes);
        
        return table;
    }

    private Table.TableCell parseTableCell(Element cellElement, boolean isHeader) {
        Table.TableCell cell = new Table.TableCell();
        cell.setHeader(isHeader);
        cell.setScope(cellElement.getAttribute("scope"));
        cell.setAlignment(cellElement.getAttribute("align"));
        cell.setClassName(cellElement.getAttribute("class"));
        
        // Parse cell content and footnotes
        String content = cellElement.getTextContent().trim();
        List<String> footnotes = new ArrayList<>();
        
        NodeList supNodes = cellElement.getElementsByTagName("sup");
        for (int i = 0; i < supNodes.getLength(); i++) {
            Element supElement = (Element) supNodes.item(i);
            footnotes.add(supElement.getTextContent().trim());
        }
        
        cell.setContent(content);
        cell.setFootnotes(footnotes);
        
        return cell;
    }

    private Abbreviation parseAbbreviation(Element abbrElement) {
        Abbreviation abbreviation = new Abbreviation();
        abbreviation.setNodeId(abbrElement.getAttribute("NODE"));
        
        // Parse heading
        NodeList hedNodes = abbrElement.getElementsByTagName("HED");
        if (hedNodes.getLength() > 0) {
            abbreviation.setHeading(hedNodes.item(0).getTextContent().trim());
        }
        
        // Parse abbreviation entries
        List<Abbreviation.AbbreviationEntry> entries = new ArrayList<>();
        NodeList pNodes = abbrElement.getElementsByTagName("P");
        for (int i = 0; i < pNodes.getLength(); i++) {
            Element pElement = (Element) pNodes.item(i);
            String content = pElement.getTextContent().trim();
            
            if (content.contains("=")) {
                String[] parts = content.split("=", 2);
                Abbreviation.AbbreviationEntry entry = new Abbreviation.AbbreviationEntry();
                entry.setAbbreviation(parts[0].trim());
                entry.setDefinition(parts[1].trim());
                
                // Check for italicized text
                NodeList iNodes = pElement.getElementsByTagName("I");
                entry.setItalicized(iNodes.getLength() > 0);
                
                entries.add(entry);
            }
        }
        
        abbreviation.setEntries(entries);
        return abbreviation;
    }

    private Index parseIndex(Element indexElement) {
        Index index = new Index();
        index.setNodeId(indexElement.getAttribute("NODE"));
        
        // Parse heading
        NodeList hedNodes = indexElement.getElementsByTagName("HED");
        if (hedNodes.getLength() > 0) {
            index.setHeading(hedNodes.item(0).getTextContent().trim());
        }
        
        // Parse editorial note
        NodeList ednoteNodes = indexElement.getElementsByTagName("EDNOTE");
        if (ednoteNodes.getLength() > 0) {
            Element ednoteElement = (Element) ednoteNodes.item(0);
            NodeList ednoteHedNodes = ednoteElement.getElementsByTagName("HED");
            if (ednoteHedNodes.getLength() > 0) {
                index.setEditorialNote(ednoteHedNodes.item(0).getTextContent().trim());
            }
        }
        
        // Parse index sections
        List<Index.IndexSection> sections = new ArrayList<>();
        Index.IndexSection currentSection = null;
        
        NodeList childNodes = indexElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String tagName = childElement.getTagName();
                
                if (tagName.equals("SECHD")) {
                    if (currentSection != null) {
                        sections.add(currentSection);
                    }
                    currentSection = new Index.IndexSection();
                    currentSection.setSectionHeader(childElement.getTextContent().trim());
                } else if (tagName.equals("ALPHHD")) {
                    if (currentSection != null) {
                        currentSection.setAlphaHeader(childElement.getTextContent().trim());
                    }
                } else if (tagName.equals("SUBJECT")) {
                    if (currentSection != null) {
                        Index.IndexEntry entry = new Index.IndexEntry();
                        entry.setSubject(childElement.getTextContent().trim());
                        currentSection.getEntries().add(entry);
                    }
                } else if (tagName.equals("SUBJ1L")) {
                    if (currentSection != null && !currentSection.getEntries().isEmpty()) {
                        Index.IndexEntry lastEntry = currentSection.getEntries().get(currentSection.getEntries().size() - 1);
                        lastEntry.getSubSubjects().add(childElement.getTextContent().trim());
                    }
                } else if (tagName.equals("PT")) {
                    if (currentSection != null && !currentSection.getEntries().isEmpty()) {
                        Index.IndexEntry lastEntry = currentSection.getEntries().get(currentSection.getEntries().size() - 1);
                        lastEntry.getPartReferences().add(childElement.getTextContent().trim());
                    }
                }
            }
        }
        
        if (currentSection != null) {
            sections.add(currentSection);
        }
        
        index.setSections(sections);
        return index;
    }

    private boolean isParagraphElement(Element element) {
        String tagName = element.getTagName();
        return tagName.equals("P") || tagName.equals("PSPACE") || 
               tagName.equals("FP") || tagName.equals("P-1");
    }
} 