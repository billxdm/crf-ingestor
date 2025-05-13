package com.ecfr.config;

import com.ecfr.dto.ecfrxml.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EcfrXmlDeserializer extends JsonDeserializer<EcfrDTO> {
    private static final Logger log = LoggerFactory.getLogger(EcfrXmlDeserializer.class);

    @Override
    public EcfrDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        EcfrDTO ecfrDTO = new EcfrDTO();

        // Parse header
        if (node.has("HEADER")) {
            JsonNode headerNode = node.get("HEADER");
            HeaderDTO header = new HeaderDTO();
            if (headerNode.has("TITLE")) {
                header.setTitle(getTextValue(headerNode, "TITLE"));
            }
            if (headerNode.has("EFFDATE")) {
                header.setEffectiveDate(getTextValue(headerNode, "EFFDATE"));
            }
            if (headerNode.has("SOURCE")) {
                header.setSource(getTextValue(headerNode, "SOURCE"));
            }
            ecfrDTO.setHeader(header);
        }

        // Parse text
        if (node.has("TEXT")) {
            JsonNode textNode = node.get("TEXT");
            TextDTO text = new TextDTO();
            
            if (textNode.has("BODY")) {
                JsonNode bodyNode = textNode.get("BODY");
                BodyDTO body = new BodyDTO();
                
                if (bodyNode.has("ECFRBRWS")) {
                    JsonNode ecfrbrwsNode = bodyNode.get("ECFRBRWS");
                    EcfrbrwsDTO ecfrbrws = new EcfrbrwsDTO();
                    ecfrbrws.setTitle(getTextValue(ecfrbrwsNode, "TITLE"));
                    ecfrbrws.setSubtitle(getTextValue(ecfrbrwsNode, "SUBTITLE"));
                    ecfrbrws.setChapter(getTextValue(ecfrbrwsNode, "CHAPTER"));
                    ecfrbrws.setSubchapter(getTextValue(ecfrbrwsNode, "SUBCHAPTER"));
                    ecfrbrws.setPart(getTextValue(ecfrbrwsNode, "PART"));
                    ecfrbrws.setSection(getTextValue(ecfrbrwsNode, "SECTION"));
                    ecfrbrws.setEffectiveDate(getTextValue(ecfrbrwsNode, "EFFDATE"));
                    body.setEcfrbrws(ecfrbrws);
                }

                // Parse divisions
                if (bodyNode.has("DIV1")) {
                    List<DivisionDTO> divisions = new ArrayList<>();
                    JsonNode divNodes = bodyNode.get("DIV1");
                    if (divNodes.isArray()) {
                        for (JsonNode divNode : divNodes) {
                            DivisionDTO division = parseDivision(divNode);
                            if (division != null) {
                                divisions.add(division);
                            }
                        }
                    } else {
                        DivisionDTO division = parseDivision(divNodes);
                        if (division != null) {
                            divisions.add(division);
                        }
                    }
                    body.setDivisions(divisions);
                }
                
                text.setBody(body);
            }
            ecfrDTO.setText(text);
        }

        return ecfrDTO;
    }

    private DivisionDTO parseDivision(JsonNode node) {
        if (node == null) return null;
        
        DivisionDTO division = new DivisionDTO();
        division.setHead(getTextValue(node, "HEAD"));
        division.setType(getTextValue(node, "TYPE"));
        division.setNumber(getTextValue(node, "NUMBER"));
        division.setVolume(getTextValue(node, "VOLUME"));
        division.setNodeId(getTextValue(node, "NODEID"));

        // Parse paragraphs
        if (node.has("P")) {
            List<ParagraphDTO> paragraphs = new ArrayList<>();
            JsonNode pNodes = node.get("P");
            if (pNodes.isArray()) {
                for (JsonNode pNode : pNodes) {
                    ParagraphDTO paragraph = parseParagraph(pNode);
                    if (paragraph != null) {
                        paragraphs.add(paragraph);
                    }
                }
            } else {
                ParagraphDTO paragraph = parseParagraph(pNodes);
                if (paragraph != null) {
                    paragraphs.add(paragraph);
                }
            }
            division.setParagraphs(paragraphs);
        }

        // Parse sub-divisions
        if (node.has("DIV2")) {
            List<DivisionDTO> subDivisions = new ArrayList<>();
            JsonNode divNodes = node.get("DIV2");
            if (divNodes.isArray()) {
                for (JsonNode divNode : divNodes) {
                    DivisionDTO subDivision = parseDivision(divNode);
                    if (subDivision != null) {
                        subDivisions.add(subDivision);
                    }
                }
            } else {
                DivisionDTO subDivision = parseDivision(divNodes);
                if (subDivision != null) {
                    subDivisions.add(subDivision);
                }
            }
            division.setSubDivisions(subDivisions);
        }

        return division;
    }

    private ParagraphDTO parseParagraph(JsonNode node) {
        if (node == null) return null;
        
        ParagraphDTO paragraph = new ParagraphDTO();
        if (node.has("SOURCE")) {
            paragraph.setSource(getTextValue(node, "SOURCE"));
        }
        if (node.has("TEXT")) {
            paragraph.setText(getTextValue(node, "TEXT"));
        }
        if (node.has("NODEID")) {
            paragraph.setNodeId(getTextValue(node, "NODEID"));
        }
        if (node.has("INDENTATION_LEVEL")) {
            paragraph.setIndentationLevel(node.get("INDENTATION_LEVEL").asInt());
        }
        if (node.has("IS_BOLD")) {
            paragraph.setIsBold(node.get("IS_BOLD").asBoolean());
        }
        if (node.has("IS_ITALIC")) {
            paragraph.setIsItalic(node.get("IS_ITALIC").asBoolean());
        }
        if (node.has("IS_UNDERLINED")) {
            paragraph.setIsUnderlined(node.get("IS_UNDERLINED").asBoolean());
        }
        return paragraph;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : null;
    }
} 