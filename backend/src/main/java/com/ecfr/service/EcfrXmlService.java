package com.ecfr.service;

import com.ecfr.dto.ecfrxml.*;
import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@Service
public class EcfrXmlService {
    private final EcfrDocumentRepository documentRepository;
    private final XmlMapper xmlMapper;

    public EcfrXmlService(EcfrDocumentRepository documentRepository, XmlMapper xmlMapper) {
        this.documentRepository = documentRepository;
        this.xmlMapper = xmlMapper;
    }

    public String reconstructXml(String titleNumber) {
        List<EcfrDocument> documents = documentRepository.findByTitleNumber(titleNumber);
        if (documents.isEmpty()) {
            throw new RuntimeException("No documents found for title " + titleNumber);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Create root element
            Element rootElement = doc.createElement("ECFR");
            doc.appendChild(rootElement);

            // Add header
            Element headerElement = doc.createElement("HEADER");
            rootElement.appendChild(headerElement);

            // Add title
            Element titleElement = doc.createElement("TITLE");
            titleElement.setTextContent(titleNumber);
            headerElement.appendChild(titleElement);

            // Add effective date
            Element effectiveDateElement = doc.createElement("EFFECTIVE-DATE");
            effectiveDateElement.setTextContent(documents.get(0).getEffectiveDate());
            headerElement.appendChild(effectiveDateElement);

            // Add text
            Element textElement = doc.createElement("TEXT");
            rootElement.appendChild(textElement);

            // Add body
            Element bodyElement = doc.createElement("BODY");
            textElement.appendChild(bodyElement);

            // Add ECFRBRWS
            Element ecfrbrwsElement = doc.createElement("ECFRBRWS");
            bodyElement.appendChild(ecfrbrwsElement);

            // Add divisions
            for (EcfrDocument document : documents) {
                EcfrDTO ecfrDTO = document.getEcfrDTO();
                if (ecfrDTO != null && ecfrDTO.getText() != null && ecfrDTO.getText().getBody() != null) {
                    BodyDTO body = ecfrDTO.getText().getBody();
                    if (body.getDivisions() != null) {
                        for (DivisionDTO division : body.getDivisions()) {
                            Element divElement = doc.createElement("DIV1");
                            divElement.setAttribute("N", division.getNumber());
                            divElement.setAttribute("TYPE", division.getType());
                            if (division.getVolume() != null) {
                                divElement.setAttribute("VOLUME", division.getVolume());
                            }
                            if (division.getHead() != null) {
                                Element headElement = doc.createElement("HEAD");
                                headElement.setTextContent(division.getHead());
                                divElement.appendChild(headElement);
                            }
                            ecfrbrwsElement.appendChild(divElement);

                            // Add paragraphs
                            if (division.getParagraphs() != null) {
                                for (ParagraphDTO paragraph : division.getParagraphs()) {
                                    Element pElement = doc.createElement("P");
                                    if (paragraph.getType() != null) {
                                        pElement.setAttribute("TYPE", paragraph.getType());
                                    }
                                    if (paragraph.getLevel() != null) {
                                        pElement.setAttribute("LEVEL", paragraph.getLevel());
                                    }
                                    if (paragraph.getAlign() != null) {
                                        pElement.setAttribute("ALIGN", paragraph.getAlign());
                                    }
                                    if (paragraph.getContent() != null) {
                                        pElement.setTextContent(paragraph.getContent());
                                    }
                                    divElement.appendChild(pElement);
                                }
                            }

                            // Add sub-divisions
                            if (division.getSubDivisions() != null) {
                                for (DivisionDTO subDivision : division.getSubDivisions()) {
                                    Element subDivElement = doc.createElement("DIV2");
                                    subDivElement.setAttribute("N", subDivision.getNumber());
                                    subDivElement.setAttribute("TYPE", subDivision.getType());
                                    if (subDivision.getVolume() != null) {
                                        subDivElement.setAttribute("VOLUME", subDivision.getVolume());
                                    }
                                    if (subDivision.getHead() != null) {
                                        Element headElement = doc.createElement("HEAD");
                                        headElement.setTextContent(subDivision.getHead());
                                        subDivElement.appendChild(headElement);
                                    }
                                    divElement.appendChild(subDivElement);

                                    // Add paragraphs for sub-division
                                    if (subDivision.getParagraphs() != null) {
                                        for (ParagraphDTO paragraph : subDivision.getParagraphs()) {
                                            Element pElement = doc.createElement("P");
                                            if (paragraph.getType() != null) {
                                                pElement.setAttribute("TYPE", paragraph.getType());
                                            }
                                            if (paragraph.getLevel() != null) {
                                                pElement.setAttribute("LEVEL", paragraph.getLevel());
                                            }
                                            if (paragraph.getAlign() != null) {
                                                pElement.setAttribute("ALIGN", paragraph.getAlign());
                                            }
                                            if (paragraph.getContent() != null) {
                                                pElement.setTextContent(paragraph.getContent());
                                            }
                                            subDivElement.appendChild(pElement);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Convert Document to String
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (Exception e) {
            log.error("Error reconstructing XML for title {}: {}", titleNumber, e.getMessage(), e);
            throw new RuntimeException("Error reconstructing XML", e);
        }
    }
} 