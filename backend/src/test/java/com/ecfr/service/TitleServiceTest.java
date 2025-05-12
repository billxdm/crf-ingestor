package com.ecfr.service;

import com.ecfr.dto.TitleDTO;
import com.ecfr.repository.TitleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TitleServiceTest {

    @Autowired
    private TitleService titleService;

    @Autowired
    private TitleRepository titleRepository;

    @Test
    public void testCreateAndRetrieveTitle() {
        // Create a new title
        TitleDTO title = new TitleDTO();
        title.setTitleNumber("48");
        title.setTitleName("Federal Acquisition Regulations System");
        title.setAgencies(Arrays.asList(
            "General Services Administration",
            "Department of Defense",
            "National Aeronautics and Space Administration"
        ));
        title.setTotalParts(52);
        title.setTotalSections(1000);
        title.setTotalWordCount(500000L);
        title.setVersion("1.0");
        title.setLastUpdated(LocalDateTime.now());

        // Save the title
        TitleDTO savedTitle = titleService.saveTitle(title);
        assertNotNull(savedTitle.getId());
        assertNotNull(savedTitle.getCreatedAt());
        assertNotNull(savedTitle.getUpdatedAt());

        // Retrieve the title
        Optional<TitleDTO> retrievedTitle = titleService.findByTitleNumber("48");
        assertTrue(retrievedTitle.isPresent());
        assertEquals("48", retrievedTitle.get().getTitleNumber());
        assertEquals("Federal Acquisition Regulations System", retrievedTitle.get().getTitleName());
        assertEquals(3, retrievedTitle.get().getAgencies().size());
        assertEquals(52, retrievedTitle.get().getTotalParts());
        assertEquals(1000, retrievedTitle.get().getTotalSections());
        assertEquals(500000L, retrievedTitle.get().getTotalWordCount());
        assertEquals("1.0", retrievedTitle.get().getVersion());

        // Clean up
        titleService.deleteTitle(savedTitle.getId());
    }
} 