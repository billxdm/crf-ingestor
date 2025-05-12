package com.ecfr.controller;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.service.EcfrIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EcfrIngestionController {
    
    private static final Logger log = LoggerFactory.getLogger(EcfrIngestionController.class);
    
    private final EcfrIngestionService ingestionService;
    
    public EcfrIngestionController(EcfrIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }
    
    @GetMapping("/ingest/{titleNumber}")
    public ResponseEntity<EcfrDTO> ingestTitle(
            @PathVariable String titleNumber,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate) {
        
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }
        
        log.info("Received request to ingest title {} for date {}", titleNumber, effectiveDate);
        EcfrDTO result = ingestionService.ingestTitle(titleNumber, effectiveDate);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/ingest/batch")
    public ResponseEntity<Map<String, Object>> ingestTitles(
            @RequestBody List<String> titleNumbers,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate) {
        
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }
        
        log.info("Received batch ingestion request for {} titles for date {}", 
                titleNumbers.size(), effectiveDate);
        
        List<EcfrDTO> results = ingestionService.ingestTitles(titleNumbers, effectiveDate).join();
        
        Map<String, Object> response = Map.of(
            "totalRequested", titleNumbers.size(),
            "successfullyProcessed", results.size(),
            "results", results
        );
        
        return ResponseEntity.ok(response);
    }
} 