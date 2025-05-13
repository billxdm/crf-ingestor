package com.ecfr.repository;

import com.ecfr.dto.ecfrxml.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EcfrRepositoryTest {

    @Autowired
    private EcfrRepository ecfrRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testCreateAndSaveTitle() {
        // Create a test Title object
        EcfrDTO ecfrDTO = createTestTitle();
        
        // Save to MongoDB
        EcfrDTO saved = ecfrRepository.save(ecfrDTO);
        
        // Verify the save
        assertNotNull(saved);
        assertNotNull(saved.getId());
        
        // Query the saved document
        EcfrDTO found = ecfrRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("43", found.getDivisions().get(0).getNumber());
        assertEquals("TITLE", found.getDivisions().get(0).getType());
        assertEquals("Title 43—Public Lands: Interior", found.getDivisions().get(0).getHead());
        
        // Test custom query
        List<EcfrDTO> byTitle = ecfrRepository.findByTitleNumber("43");
        assertNotNull(byTitle);
        assertFalse(byTitle.isEmpty());
        assertEquals("43", byTitle.get(0).getText().getBody().getEcfrbrws().getTitle());
        
        // Clean up
        ecfrRepository.deleteById(saved.getId());
    }

    private EcfrDTO createTestTitle() {
        EcfrDTO ecfrDTO = new EcfrDTO();
        
        // Create header
        HeaderDTO header = new HeaderDTO();
        header.setTitle("Title 43");
        header.setEffectiveDate("2024-05-11");
        ecfrDTO.setHeader(header);
        
        // Create text body
        TextDTO text = new TextDTO();
        BodyDTO body = new BodyDTO();
        EcfrbrwsDTO ecfrbrws = new EcfrbrwsDTO();
        ecfrbrws.setTitle("43");
        ecfrbrws.setSubtitle("A");
        body.setEcfrbrws(ecfrbrws);
        text.setBody(body);
        ecfrDTO.setText(text);
        
        // Create division (Title)
        DivisionDTO division = new DivisionDTO();
        division.setNumber("43");
        division.setType("TITLE");
        division.setHead("Title 43—Public Lands: Interior");
        
        // Create authority
        AuthorityDTO authority = new AuthorityDTO();
        authority.setHeading("Authority:");
        authority.setParagraphSpace("Sec. 5, 23 Stat. 101; 43 U.S.C. 1464.");
        division.setAuthority(authority);
        
        // Create sub-division (Subtitle)
        DivisionDTO subDivision = new DivisionDTO();
        subDivision.setNumber("A");
        subDivision.setType("SUBTITLE");
        subDivision.setHead("Subtitle A—Office of the Secretary of the Interior");
        
        // Add sub-division to division
        division.setSubDivisions(Arrays.asList(subDivision));
        
        // Add division to ECFR
        ecfrDTO.setDivisions(Arrays.asList(division));
        
        return ecfrDTO;
    }
} 