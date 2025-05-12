package com.ecfr.dto.ecfrxml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EcfrDTOXmlTest {

    private XmlMapper xmlMapper;

    @BeforeEach
    public void setup() {
        xmlMapper = new XmlMapper();
        xmlMapper.setDefaultUseWrapper(false);
    }

    @Test
    public void testLoadXml() throws Exception {
        // Sample XML that matches our DTO structure
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <DLPSTEXTCLASS>
                <HEADER>
                    <TITLE>Title 1 - General Provisions</TITLE>
                    <EFFDATE>2024-01-01</EFFDATE>
                    <SOURCE>Federal Register</SOURCE>
                </HEADER>
                <TEXT>
                    <BODY>
                        <ECFRBRWS>
                            <TITLE>Title 1</TITLE>
                            <SUBTITLE>Subtitle A</SUBTITLE>
                            <CHAPTER>1</CHAPTER>
                            <SUBCHAPTER>A</SUBCHAPTER>
                            <PART>1</PART>
                            <SECTION>1.1</SECTION>
                            <EFFDATE>2024-01-01</EFFDATE>
                            <DIV1>
                                <HEAD>Title 1 - General Provisions</HEAD>
                            </DIV1>
                        </ECFRBRWS>
                    </BODY>
                </TEXT>
            </DLPSTEXTCLASS>
            """;

        // Parse XML into DTO
        EcfrDTO ecfrDTO = xmlMapper.readValue(xml, EcfrDTO.class);

        // Verify the structure
        assertNotNull(ecfrDTO);
        assertNotNull(ecfrDTO.getHeader());
        assertEquals("Title 1 - General Provisions", ecfrDTO.getHeader().getTitle());
        assertEquals("2024-01-01", ecfrDTO.getHeader().getEffectiveDate());
        assertEquals("Federal Register", ecfrDTO.getHeader().getSource());

        assertNotNull(ecfrDTO.getText());
        assertNotNull(ecfrDTO.getText().getBody());
        assertNotNull(ecfrDTO.getText().getBody().getEcfrbrws());

        EcfrbrwsDTO ecfrbrws = ecfrDTO.getText().getBody().getEcfrbrws();
        assertEquals("Title 1", ecfrbrws.getTitle());
        assertEquals("Subtitle A", ecfrbrws.getSubtitle());
        assertEquals("1", ecfrbrws.getChapter());
        assertEquals("A", ecfrbrws.getSubchapter());
        assertEquals("1", ecfrbrws.getPart());
        assertEquals("1.1", ecfrbrws.getSection());
        assertEquals("2024-01-01", ecfrbrws.getEffectiveDate());

        assertNotNull(ecfrbrws.getDivisions());
        assertEquals(1, ecfrbrws.getDivisions().size());
        assertEquals("Title 1 - General Provisions", ecfrbrws.getDivisions().get(0).getHead());
    }
} 