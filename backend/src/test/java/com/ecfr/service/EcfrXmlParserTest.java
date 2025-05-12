package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EcfrXmlParserTest {

    @Autowired
    private EcfrXmlParser parser;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testParseEcfrXml() {
        // Sample XML content
        String sampleXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <ECFR>
                <TITLE n="1" type="title">
                    <CHAPTER n="I" type="chapter">
                        <SUBCHAPTER n="A" type="subchapter">
                            <PART n="1" type="part">
                                <SECTION n="1.1" type="section">
                                    <HD n="1.1" type="section">Section 1.1</HD>
                                    <P n="1" type="paragraph">This is a test paragraph.</P>
                                </SECTION>
                            </PART>
                        </SUBCHAPTER>
                    </CHAPTER>
                </TITLE>
            </ECFR>
            """;

        // Mock the RestTemplate response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(sampleXml);

        // Test parsing from URL
        EcfrDTO result = parser.parseEcfrXml("http://example.com/ecfr.xml");
        assertNotNull(result);
        assertNotNull(result.getText());
        assertNotNull(result.getText().getBody());
        assertNotNull(result.getText().getBody().getEcfrbrws());
        assertNotNull(result.getText().getBody().getEcfrbrws().getTitle());
        assertEquals("1", result.getText().getBody().getEcfrbrws().getTitle());

        // Test parsing from string
        EcfrDTO resultFromString = parser.parseEcfrXmlFromString(sampleXml);
        assertNotNull(resultFromString);
        assertNotNull(resultFromString.getText());
        assertNotNull(resultFromString.getText().getBody());
        assertNotNull(resultFromString.getText().getBody().getEcfrbrws());
        assertNotNull(resultFromString.getText().getBody().getEcfrbrws().getTitle());
        assertEquals("1", resultFromString.getText().getBody().getEcfrbrws().getTitle());
    }
} 