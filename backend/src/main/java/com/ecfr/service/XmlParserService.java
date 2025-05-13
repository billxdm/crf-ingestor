package com.ecfr.service;

import com.ecfr.dto.ecfrxml.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XmlParserService {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserService.class);

    public EcfrDTO parseEcfrXmlFromString(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            
            Element root = document.getDocumentElement();
            EcfrDTO ecfrDTO = new EcfrDTO();
            
            // Parse text element
            NodeList textNodes = root.getElementsByTagName("TEXT");
            if (textNodes.getLength() > 0) {
                Element textElement = (Element) textNodes.item(0);
                TextDTO textDTO = new TextDTO();
                
                // Parse body element
                NodeList bodyNodes = textElement.getElementsByTagName("BODY");
                if (bodyNodes.getLength() > 0) {
                    Element bodyElement = (Element) bodyNodes.item(0);
                    BodyDTO bodyDTO = new BodyDTO();
                    
                    // Parse ECFRBRWS element
                    NodeList ecfrbrwsNodes = bodyElement.getElementsByTagName("ECFRBRWS");
                    if (ecfrbrwsNodes.getLength() > 0) {
                        Element ecfrbrwsElement = (Element) ecfrbrwsNodes.item(0);
                        EcfrbrwsDTO ecfrbrwsDTO = new EcfrbrwsDTO();
                        ecfrbrwsDTO.setTitle(ecfrbrwsElement.getAttribute("TITLE"));
                        bodyDTO.setEcfrbrws(ecfrbrwsDTO);
                    }
                    
                    textDTO.setBody(bodyDTO);
                }
                
                ecfrDTO.setText(textDTO);
            }
            
            // Parse divisions
            List<DivisionDTO> divisions = new ArrayList<>();
            NodeList divNodes = root.getElementsByTagName("DIV1");
            for (int i = 0; i < divNodes.getLength(); i++) {
                Element divElement = (Element) divNodes.item(i);
                DivisionDTO division = parseDivision(divElement);
                if (division != null) {
                    divisions.add(division);
                }
            }
            ecfrDTO.setDivisions(divisions);
            
            return ecfrDTO;
        } catch (Exception e) {
            logger.error("Error parsing XML content", e);
            throw new RuntimeException("Failed to parse XML content", e);
        }
    }

    private DivisionDTO parseDivision(Element divElement) {
        DivisionDTO division = new DivisionDTO();
        division.setNodeId(divElement.getAttribute("NODE"));
        division.setNumber(divElement.getAttribute("N"));
        division.setType(divElement.getAttribute("TYPE"));
        
        // Parse heading
        NodeList headNodes = divElement.getElementsByTagName("HEAD");
        if (headNodes.getLength() > 0) {
            division.setHead(headNodes.item(0).getTextContent().trim());
        }
        
        // Parse paragraphs
        List<ParagraphDTO> paragraphs = new ArrayList<>();
        NodeList pNodes = divElement.getElementsByTagName("P");
        for (int i = 0; i < pNodes.getLength(); i++) {
            Element pElement = (Element) pNodes.item(i);
            ParagraphDTO paragraph = parseParagraph(pElement);
            if (paragraph != null) {
                paragraphs.add(paragraph);
            }
        }
        division.setParagraphs(paragraphs);
        
        // Parse sub-divisions recursively
        List<DivisionDTO> subDivisions = new ArrayList<>();
        NodeList subDivNodes = divElement.getElementsByTagName("DIV2");
        for (int i = 0; i < subDivNodes.getLength(); i++) {
            Element subDivElement = (Element) subDivNodes.item(i);
            DivisionDTO subDivision = parseDivision(subDivElement);
            if (subDivision != null) {
                subDivisions.add(subDivision);
            }
        }
        division.setSubDivisions(subDivisions);
        
        return division;
    }

    private ParagraphDTO parseParagraph(Element pElement) {
        ParagraphDTO paragraph = new ParagraphDTO();
        paragraph.setNodeId(pElement.getAttribute("NODE"));
        paragraph.setContent(pElement.getTextContent().trim());
        
        // Parse formatting attributes
        paragraph.setIndentationLevel(parseIndentationLevel(pElement));
        paragraph.setClasses(parseClasses(pElement));
        paragraph.setAttributes(parseAttributes(pElement));
        
        // Parse nested elements
        paragraph.setCitations(parseCitations(pElement));
        paragraph.setGraphics(parseGraphics(pElement));
        paragraph.setFootnotes(parseFootnotes(pElement));
        
        // Set metadata
        paragraph.setIsBold(hasClass(pElement, "bold") || hasClass(pElement, "strong"));
        paragraph.setIsItalic(hasClass(pElement, "italic") || hasClass(pElement, "em"));
        paragraph.setIsUnderlined(hasClass(pElement, "underline"));
        
        return paragraph;
    }

    private int parseIndentationLevel(Element element) {
        String indent = element.getAttribute("INDENT");
        if (indent != null && !indent.isEmpty()) {
            try {
                return Integer.parseInt(indent);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private List<String> parseClasses(Element element) {
        List<String> classes = new ArrayList<>();
        String classAttr = element.getAttribute("CLASS");
        if (classAttr != null && !classAttr.isEmpty()) {
            for (String c : classAttr.split("\\s+")) {
                classes.add(c);
            }
        }
        return classes;
    }

    private Map<String, String> parseAttributes(Element element) {
        Map<String, String> attributes = new HashMap<>();
        org.w3c.dom.NamedNodeMap nodeMap = element.getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            attributes.put(node.getNodeName(), node.getNodeValue());
        }
        return attributes;
    }

    private List<CitationDTO> parseCitations(Element element) {
        List<CitationDTO> citations = new ArrayList<>();
        NodeList citationNodes = element.getElementsByTagName("CITA");
        for (int i = 0; i < citationNodes.getLength(); i++) {
            Element citationElement = (Element) citationNodes.item(i);
            CitationDTO citation = new CitationDTO();
            citation.setType(citationElement.getAttribute("TYPE"));
            citation.setContent(citationElement.getTextContent().trim());
            citation.setReference(citationElement.getAttribute("REF"));
            citation.setDate(citationElement.getAttribute("DATE"));
            citations.add(citation);
        }
        return citations;
    }

    private List<GraphicDTO> parseGraphics(Element element) {
        List<GraphicDTO> graphics = new ArrayList<>();
        NodeList graphicNodes = element.getElementsByTagName("GRAPHIC");
        for (int i = 0; i < graphicNodes.getLength(); i++) {
            Element graphicElement = (Element) graphicNodes.item(i);
            GraphicDTO graphic = new GraphicDTO();
            graphic.setType(graphicElement.getAttribute("TYPE"));
            graphic.setSrc(graphicElement.getAttribute("SRC"));
            graphic.setAlt(graphicElement.getAttribute("ALT"));
            graphic.setCaption(graphicElement.getAttribute("CAPTION"));
            graphic.setAttributes(parseAttributes(graphicElement));
            graphics.add(graphic);
        }
        return graphics;
    }

    private List<FootnoteDTO> parseFootnotes(Element element) {
        List<FootnoteDTO> footnotes = new ArrayList<>();
        NodeList footnoteNodes = element.getElementsByTagName("FOOTNOTE");
        for (int i = 0; i < footnoteNodes.getLength(); i++) {
            Element footnoteElement = (Element) footnoteNodes.item(i);
            FootnoteDTO footnote = new FootnoteDTO();
            footnote.setNumber(footnoteElement.getAttribute("N"));
            footnote.setContent(footnoteElement.getTextContent().trim());
            footnote.setReference(footnoteElement.getAttribute("REF"));
            footnotes.add(footnote);
        }
        return footnotes;
    }

    private boolean hasClass(Element element, String className) {
        String classAttr = element.getAttribute("CLASS");
        return classAttr != null && classAttr.contains(className);
    }
} 