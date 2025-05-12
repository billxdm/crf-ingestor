package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.repository.EcfrRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("test")
public class EcfrIngestionServiceTest {

    @Autowired
    private EcfrIngestionService ingestionService;

    @Autowired
    private EcfrRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
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

    @Test
    public void testIngestTitle43Xml() throws Exception {
        // Create a test XML file with title 43 content
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ECFR>\n" +
                "  <DIV1 N=\"43\" TYPE=\"TITLE\">\n" +
                "    <HEAD>Title 43—Public Lands: Interior</HEAD>\n" +
                "    <DIV2 N=\"I\" TYPE=\"SUBTITLE\">\n" +
                "      <HEAD>Subtitle I—Public Lands: General</HEAD>\n" +
                "      <DIV3 N=\"1\" TYPE=\"CHAPTER\">\n" +
                "        <HEAD>Chapter 1—Bureau of Land Management, Department of the Interior</HEAD>\n" +
                "        <DIV4 N=\"1\" TYPE=\"SUBCHAPTER\">\n" +
                "          <HEAD>Subchapter A—General Management (1000)</HEAD>\n" +
                "          <DIV5 N=\"1000\" TYPE=\"PART\">\n" +
                "            <HEAD>Part 1000—Administrative Matters</HEAD>\n" +
                "            <DIV6 N=\"1000.1\" TYPE=\"SECTION\">\n" +
                "              <HEAD>§ 1000.1   Purpose.</HEAD>\n" +
                "              <P>This part contains the rules and regulations of the Bureau of Land Management.</P>\n" +
                "            </DIV6>\n" +
                "          </DIV5>\n" +
                "        </DIV4>\n" +
                "      </DIV3>\n" +
                "    </DIV2>\n" +
                "  </DIV1>\n" +
                "</ECFR>";
        
        // Write XML to a temporary file
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-title43", ".xml");
        try {
            // Write the XML content with UTF-8 encoding
            java.nio.file.Files.write(tempFile, xmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String filePath = tempFile.toAbsolutePath().toString();

            // Verify the file was written correctly
            String writtenContent = new String(java.nio.file.Files.readAllBytes(tempFile), java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(writtenContent.startsWith("<?xml"), "XML file should start with XML declaration");
            assertTrue(writtenContent.contains("<ECFR>"), "XML file should contain ECFR root element");

            // Test ingestion
            ingestionService.ingestEcfrData(filePath).join();

            // Verify repository interactions
            verify(repository, times(1)).save(any(EcfrDTO.class));
        } finally {
            // Clean up temp file
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testIngestLocalXmlFile() throws Exception {
        // Read the XML file
        String filePath = "/Users/billxiong/3213.xml";
        assertTrue(Files.exists(Paths.get(filePath)), "XML file should exist at: " + filePath);

        // Read and validate XML content
        String xmlContent = Files.readString(Paths.get(filePath));
        assertTrue(xmlContent.trim().startsWith("<?xml"), "File should start with XML declaration");
        assertTrue(xmlContent.contains("<ECFR>"), "File should contain ECFR root element");

        // Ingest the XML content
        EcfrDTO result = ingestionService.ingestEcfrData(xmlContent).join();
        assertNotNull(result, "Ingested data should not be null");
        assertNotNull(result.getId(), "Document should have an ID");
        assertNotNull(result.getTitleNumber(), "Document should have a title number");

        // Verify MongoDB storage
        Optional<EcfrDTO> savedOpt = repository.findByTitleNumber(result.getTitleNumber());
        assertTrue(savedOpt.isPresent(), "Document should be saved in MongoDB");
        
        EcfrDTO saved = savedOpt.get();
        assertEquals(result.getId(), saved.getId(), "Saved document ID should match");
        assertEquals(result.getTitleNumber(), saved.getTitleNumber(), "Saved title number should match");
        
        // Validate document structure
        assertNotNull(saved.getText(), "Saved document should have text");
        assertNotNull(saved.getText().getBody(), "Saved document should have body");
        assertNotNull(saved.getText().getBody().getEcfrbrws(), "Saved document should have ECFR browse info");
        
        // Log document details
        System.out.println("Document ID: " + saved.getId());
        System.out.println("Title Number: " + saved.getTitleNumber());
        System.out.println("Header: " + saved.getHeader());
        System.out.println("ECFR Browse Info: " + saved.getText().getBody().getEcfrbrws());
    }

    @Test
    public void testIngestInvalidXml() {
        String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><invalid>content</invalid>";
        
        assertThrows(RuntimeException.class, () -> {
            ingestionService.ingestEcfrData(invalidXml).join();
        });
    }

    @Test
    public void testIngestEmptyXml() {
        String emptyXml = "";
        
        assertThrows(IllegalArgumentException.class, () -> {
            ingestionService.ingestEcfrData(emptyXml).join();
        });
    }

    @Test
    public void testUpdateExistingDocument() throws Exception {
        // First ingestion
        String filePath = "/Users/billxiong/3213.xml";
        String xmlContent = Files.readString(Paths.get(filePath));
        EcfrDTO firstResult = ingestionService.ingestEcfrData(xmlContent).join();
        
        // Second ingestion of the same document
        EcfrDTO secondResult = ingestionService.ingestEcfrData(xmlContent).join();
        
        // Verify both documents have the same ID
        assertEquals(firstResult.getId(), secondResult.getId(), "Updated document should have the same ID");
        
        // Verify only one document exists in MongoDB
        assertEquals(1, repository.count(), "Should only have one document in MongoDB");
    }
} 