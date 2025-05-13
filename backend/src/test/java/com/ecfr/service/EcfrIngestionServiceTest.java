package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.dto.ecfrxml.TextDTO;
import com.ecfr.dto.ecfrxml.BodyDTO;
import com.ecfr.dto.ecfrxml.EcfrbrwsDTO;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.Collections;
import org.mockito.InjectMocks;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
@ActiveProfiles("test")
public class EcfrIngestionServiceTest {

    @Autowired
    private EcfrRepository repository;

    @InjectMocks
    private EcfrIngestionService service;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String xmlContent;

    @BeforeEach
    public void setUp() {
        xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ECFR>\n" +
                "  <HEADER>\n" +
                "    <TITLE>43</TITLE>\n" +
                "    <EFFECTIVE-DATE>2024-01-01</EFFECTIVE-DATE>\n" +
                "  </HEADER>\n" +
                "  <TEXT>\n" +
                "    <BODY>\n" +
                "      <ECFRBRWS>\n" +
                "        <TITLE>43</TITLE>\n" +
                "      </ECFRBRWS>\n" +
                "    </BODY>\n" +
                "  </TEXT>\n" +
                "</ECFR>";
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(new Query(), "ecfr_documents");
    }

    @Test
    public void testIngestSingleTitle() {
        // Test with Title 31 (Money and Finance)
        String titleNumber = "31";
        LocalDate effectiveDate = LocalDate.of(2025, 5, 11);

        EcfrDTO result = service.ingestTitle(titleNumber, effectiveDate);

        assertNotNull(result);
        assertNotNull(result.getText());
        assertNotNull(result.getText().getBody());
        assertNotNull(result.getText().getBody().getEcfrbrws());
        assertEquals(titleNumber, result.getText().getBody().getEcfrbrws().getTitle());
        
        // Verify MongoDB storage
        List<EcfrDTO> savedDocs = repository.findByTitleNumber(titleNumber);
        assertFalse(savedDocs.isEmpty());
        EcfrDTO saved = savedDocs.get(savedDocs.size() - 1);
        assertEquals(result.getId(), saved.getId());
    }

    @Test
    public void testIngestMultipleTitles() {
        List<String> titleNumbers = Arrays.asList("31", "32");
        LocalDate effectiveDate = LocalDate.of(2025, 5, 11);

        List<EcfrDTO> results = service.ingestTitles(titleNumbers, effectiveDate).join();

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
            service.ingestTitle(invalidTitleNumber, effectiveDate);
        });
    }

    @Test
    public void testFutureDate() {
        String titleNumber = "31";
        LocalDate futureDate = LocalDate.now().plusYears(1);

        assertThrows(RuntimeException.class, () -> {
            service.ingestTitle(titleNumber, futureDate);
        });
    }

    @Test
    public void testIngestTitle() {
        // Test parameters
        String titleNumber = "43";
        LocalDate effectiveDate = LocalDate.of(2025, 5, 5);

        // Ingest the title
        EcfrDTO ingested = service.ingestTitle(titleNumber, effectiveDate);

        // Verify the ingested data
        assertNotNull(ingested, "Ingested data should not be null");
        assertNotNull(ingested.getText());
        assertNotNull(ingested.getText().getBody());
        assertNotNull(ingested.getText().getBody().getEcfrbrws());
        assertNotNull(ingested.getText().getBody().getEcfrbrws().getTitle(), "Title should be present in the ingested data");

        // Verify that the data is stored in MongoDB
        List<EcfrDTO> savedDocs = repository.findByTitleNumber(titleNumber);
        assertFalse(savedDocs.isEmpty());
        EcfrDTO saved = savedDocs.get(savedDocs.size() - 1);
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
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());

        // Test ingestion
        service.ingestEcfrData(filePath);

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
                "      </DIV3>\n" +
                "    </DIV2>\n" +
                "  </DIV1>\n" +
                "</ECFR>";
        
        // Write XML to a temporary file
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-ecfr", ".xml");
        java.nio.file.Files.write(tempFile, xmlContent.getBytes());
        String filePath = tempFile.toAbsolutePath().toString();

        // Mock repository behavior
        when(repository.save(any(EcfrDTO.class))).thenReturn(new EcfrDTO());
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());

        // Test ingestion
        service.ingestEcfrData(filePath);

        // Verify repository interactions
        verify(repository, times(1)).save(any(EcfrDTO.class));
        verify(repository, times(1)).findByTitleNumber(anyString());

        // Clean up temp file
        java.nio.file.Files.deleteIfExists(tempFile);
    }

    @Test
    public void testIngestLocalXmlFile() throws Exception {
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
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());

        // Test ingestion
        service.ingestEcfrData(filePath);

        // Verify repository interactions
        verify(repository, times(1)).save(any(EcfrDTO.class));
        verify(repository, times(1)).findByTitleNumber(anyString());

        // Clean up temp file
        java.nio.file.Files.deleteIfExists(tempFile);
    }

    @Test
    public void testIngestInvalidXml() {
        String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ECFR>\n" +
                "  <INVALID>\n" +
                "    <TAG>Invalid content</TAG>\n" +
                "  </INVALID>\n" +
                "</ECFR>";
        
        assertThrows(RuntimeException.class, () -> {
            service.ingestEcfrData(invalidXml).join();
        });
    }

    @Test
    public void testIngestEmptyXml() {
        String emptyXml = "";
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.ingestEcfrData(emptyXml).join();
        });
    }

    @Test
    public void testUpdateExistingDocument() throws Exception {
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
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());

        // Test ingestion
        service.ingestEcfrData(filePath);

        // Verify repository interactions
        verify(repository, times(1)).save(any(EcfrDTO.class));
        verify(repository, times(1)).findByTitleNumber(anyString());

        // Clean up temp file
        java.nio.file.Files.deleteIfExists(tempFile);
    }

    @Test
    public void testProcessSpecificXmlFile() throws Exception {
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
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());

        // Test ingestion
        service.ingestEcfrData(filePath);

        // Verify repository interactions
        verify(repository, times(1)).save(any(EcfrDTO.class));
        verify(repository, times(1)).findByTitleNumber(anyString());

        // Clean up temp file
        java.nio.file.Files.deleteIfExists(tempFile);
    }

    @Test
    public void testIngestExistingDocument() throws Exception {
        // Create an existing document
        EcfrDTO existingDoc = new EcfrDTO();
        TextDTO text = new TextDTO();
        BodyDTO body = new BodyDTO();
        EcfrbrwsDTO ecfrbrws = new EcfrbrwsDTO();
        ecfrbrws.setTitle("43");
        body.setEcfrbrws(ecfrbrws);
        text.setBody(body);
        existingDoc.setText(text);

        // Create a new document
        EcfrDTO newDoc = new EcfrDTO();
        newDoc.setText(text);

        // Mock repository behavior for existing document
        List<EcfrDTO> existingDocs = Collections.singletonList(existingDoc);
        when(repository.findByTitleNumber(anyString())).thenReturn(existingDocs);
        when(repository.save(any(EcfrDTO.class))).thenReturn(newDoc);

        // Test ingestion with existing document
        CompletableFuture<EcfrDTO> future = service.ingestEcfrData(xmlContent);
        EcfrDTO result = future.join();
        assertNotNull(result);
        assertEquals("43", result.getText().getBody().getEcfrbrws().getTitle());

        // Verify repository calls
        verify(repository).findByTitleNumber("43");
        verify(repository).save(any(EcfrDTO.class));
    }

    @Test
    public void testIngestNewDocument() throws Exception {
        // Create test document
        EcfrDTO ecfrDTO = new EcfrDTO();
        TextDTO text = new TextDTO();
        BodyDTO body = new BodyDTO();
        EcfrbrwsDTO ecfrbrws = new EcfrbrwsDTO();
        ecfrbrws.setTitle("43");
        body.setEcfrbrws(ecfrbrws);
        text.setBody(body);
        ecfrDTO.setText(text);

        // Mock repository behavior
        when(repository.findByTitleNumber(anyString())).thenReturn(Collections.emptyList());
        when(repository.save(any(EcfrDTO.class))).thenReturn(ecfrDTO);

        // Test ingestion
        CompletableFuture<EcfrDTO> future = service.ingestEcfrData(xmlContent);
        EcfrDTO result = future.join();
        assertNotNull(result);
        assertEquals("43", result.getText().getBody().getEcfrbrws().getTitle());

        // Verify repository calls
        verify(repository).findByTitleNumber("43");
        verify(repository).save(any(EcfrDTO.class));
    }
} 