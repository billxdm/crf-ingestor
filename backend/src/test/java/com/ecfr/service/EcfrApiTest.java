package com.ecfr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EcfrApiTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testEcfrApiCall() {
        // Test parameters
        String titleNumber = "43";
        LocalDate effectiveDate = LocalDate.of(2025, 5, 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Build URL
        String url = String.format("https://www.ecfr.gov/api/versioner/v1/full/%s/title-%s.xml",
            effectiveDate.format(formatter),
            titleNumber);
        
        System.out.println("Testing API call to: " + url);
        
        // Make the API call
        String response = restTemplate.getForObject(url, String.class);
        
        // Verify response
        assertNotNull(response, "Response should not be null");
        assertTrue(response.contains("<?xml"), "Response should be XML");
        assertTrue(response.contains("<ECFR>"), "Response should contain ECFR root element");
        assertTrue(response.contains("<DIV1 N=\"" + titleNumber + "\" TYPE=\"TITLE\""), 
            "Response should contain the requested title as DIV1");
        
        // Print first 500 characters of response for inspection
        System.out.println("Response preview:");
        System.out.println(response.substring(0, Math.min(500, response.length())));
    }
} 