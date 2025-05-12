package com.ecfr.service;

import com.ecfr.config.MongoTestConfig;
import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.repository.EcfrRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = MongoTestConfig.class)
public class EcfrIngestionServiceTest {

    @Autowired
    private EcfrIngestionService ingestionService;

    @MockBean
    private EcfrRepository repository;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        repository.deleteAll();
    }

    @Test
    public void testIngestSingleTitle() {
        // Test with Title 31 (Money and Finance)
        String titleNumber = "31";
        LocalDate effectiveDate = LocalDate.of(2025, 5, 11);

        EcfrDTO result = ingestionService.ingestTitle(titleNumber, effectiveDate);

        assertNotNull(result);
        assertNotNull(result.getText());
        assertNotNull(result.getText().getBody());
        assertNotNull(result.getText().getBody().getEcfrbrws());
        assertEquals(titleNumber, result.getText().getBody().getEcfrbrws().getTitle());
        
        // Verify MongoDB storage
        Optional<EcfrDTO> savedOpt = repository.findByTitleNumber(titleNumber);
        assertTrue(savedOpt.isPresent());
        EcfrDTO saved = savedOpt.get();
        assertEquals(result.getId(), saved.getId());
    }

    @Test
    public void testIngestMultipleTitles() {
        List<String> titleNumbers = Arrays.asList("31", "32");
        LocalDate effectiveDate = LocalDate.of(2025, 5, 11);

        List<EcfrDTO> results = ingestionService.ingestTitles(titleNumbers, effectiveDate).join();

        assertNotNull(results);
        assertEquals(2, results.size());
        
        // Verify each title was processed correctly
        results.forEach(result -> {
            assertNotNull(result);
            assertNotNull(result.getText());
            assertNotNull(result.getText().getBody());
            assertNotNull(result.getText().getBody().getEcfrbrws());
            assertTrue(titleNumbers.contains(result.getText().getBody().getEcfrbrws().getTitle()));
        });
    }

    @Test
    public void testInvalidTitleNumber() {
        String invalidTitleNumber = "999"; // Non-existent title
        LocalDate effectiveDate = LocalDate.of(2025, 5, 11);

        assertThrows(RuntimeException.class, () -> {
            ingestionService.ingestTitle(invalidTitleNumber, effectiveDate);
        });
    }

    @Test
    public void testFutureDate() {
        String titleNumber = "31";
        LocalDate futureDate = LocalDate.now().plusYears(1);

        assertThrows(RuntimeException.class, () -> {
            ingestionService.ingestTitle(titleNumber, futureDate);
        });
    }

    @Test
    public void testIngestTitle() {
        // Test parameters
        String titleNumber = "43";
        LocalDate effectiveDate = LocalDate.of(2025, 5, 5);

        // Ingest the title
        EcfrDTO ingested = ingestionService.ingestTitle(titleNumber, effectiveDate);

        // Verify the ingested data
        assertNotNull(ingested, "Ingested data should not be null");
        assertNotNull(ingested.getText());
        assertNotNull(ingested.getText().getBody());
        assertNotNull(ingested.getText().getBody().getEcfrbrws());
        assertNotNull(ingested.getText().getBody().getEcfrbrws().getTitle(), "Title should be present in the ingested data");

        // Verify that the data is stored in MongoDB
        Optional<EcfrDTO> savedOpt = repository.findByTitleNumber(titleNumber);
        assertTrue(savedOpt.isPresent());
        EcfrDTO saved = savedOpt.get();
        assertNotNull(saved, "Data should be saved in MongoDB");
        assertNotNull(saved.getText().getBody().getEcfrbrws().getTitle(), "Title should be present in the saved data");
    }

    @Test
    public void testIngestEcfrData() throws Exception {
        // Create a test XML file
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ECFR>\n" +
                "  <DIV1 N=\"43\" TYPE=\"TITLE\">\n" +
                "    <HEAD>Title 43—Public Lands: Interior</HEAD>\n" +
                "  </DIV1>\n" +
                "</ECFR>";
        
        // Write XML to a temporary file
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-ecfr", ".xml");
        java.nio.file.Files.write(tempFile, xmlContent.getBytes());
        String filePath = tempFile.toAbsolutePath().toString();

        // Mock repository behavior
        when(repository.save(any(EcfrDTO.class))).thenReturn(new EcfrDTO());
        when(repository.findByTitleNumber(anyString())).thenReturn(Optional.of(new EcfrDTO()));

        // Test ingestion
        ingestionService.ingestEcfrData(filePath);

        // Verify repository interactions
        verify(repository, times(1)).save(any(EcfrDTO.class));
        verify(repository, times(1)).findByTitleNumber(anyString());

        // Clean up temp file
        java.nio.file.Files.deleteIfExists(tempFile);
    }
} 