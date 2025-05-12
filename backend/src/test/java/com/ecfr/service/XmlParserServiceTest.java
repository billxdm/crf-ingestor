package com.ecfr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class XmlParserServiceTest {

    @Autowired
    private XmlParserService xmlParserService;

    // @Test
    // public void testParseSection() {
    //     String xmlContent = "<DIV8 N=\"§ 151.101\" NODE=\"5:1.0.1.2.12.0.1.1\" TYPE=\"SECTION\">" +
    //             "<HEAD>§ 151.101   Definitions.</HEAD>" +
    //             "<P>In this part: </P>" +
    //             "<P>(a) <I>State</I> means a State or territory or possession of the United States. </P>" +
    //             "<P>(b) <I>State or local agency</I> means:</P>" +
    //             "<P>(1) The executive branch of a State, municipality, or other political subdivision of a State, or an agency or department thereof; or</P>" +
    //             "<P>(2) The executive branch of the District of Columbia, or an agency or department thereof.</P>" +
    //             "<P>(c) <I>Federal agency</I> means an executive agency or other agency of the United States, but does not include a member bank of the Federal Reserve System; </P>" +
    //             "<CITA TYPE=\"N\">[40 FR 42733, Sept. 16, 1975, as amended at 79 FR 25484, May 5, 2014]</CITA>" +
    //             "</DIV8>";
    //
    //     CfrSection section = xmlParserService.parseSectionXml(xmlContent);
    //
    //     // Verify basic section information
    //     assertEquals("5:1.0.1.2.12.0.1.1", section.getNodeId());
    //     assertEquals("§ 151.101", section.getSectionNumber());
    //     assertEquals("§ 151.101   Definitions.", section.getName());
    //
    //     // Verify paragraphs
    //     assertNotNull(section.getParagraphs());
    //     assertFalse(section.getParagraphs().isEmpty());
    //
    //     // Verify first paragraph
    //     CfrSection.Paragraph firstParagraph = section.getParagraphs().get(0);
    //     assertEquals("In this part:", firstParagraph.getContent());
    //     assertEquals("P", firstParagraph.getType());
    //
    //     // Verify numbered paragraph with italicized text
    //     CfrSection.Paragraph stateParagraph = section.getParagraphs().get(1);
    //     assertEquals("a", stateParagraph.getNumber());
    //     assertTrue(stateParagraph.isItalicized());
    //     assertTrue(stateParagraph.getContent().contains("State"));
    //
    //     // Verify citation
    //     assertNotNull(section.getCitations());
    //     assertFalse(section.getCitations().isEmpty());
    //     CfrSection.Citation citation = section.getCitations().get(0);
    //     assertEquals("N", citation.getType());
    //     assertTrue(citation.getContent().contains("40 FR 42733"));
    //     assertNotNull(citation.getEffectiveDate());
    // }
} 