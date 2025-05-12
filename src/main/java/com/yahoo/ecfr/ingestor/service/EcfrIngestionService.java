package com.yahoo.ecfr.ingestor.service;

import com.yahoo.ecfr.ingestor.model.EcfrDocument;
import com.yahoo.ecfr.ingestor.repository.EcfrDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EcfrIngestionService {
    private static final Logger logger = LoggerFactory.getLogger(EcfrIngestionService.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private EcfrDocumentRepository documentRepository;

    @Autowired
    private BatchIngestionService batchIngestionService;

    @Async
    public CompletableFuture<Integer> ingestDocuments(List<EcfrDocument> documents) {
        AtomicInteger totalProcessed = new AtomicInteger(0);
        List<CompletableFuture<Integer>> batchFutures = new ArrayList<>();
        
        try {
            // Split documents into batches
            for (int i = 0; i < documents.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, documents.size());
                List<EcfrDocument> batch = documents.subList(i, endIndex);
                
                CompletableFuture<Integer> batchFuture = batchIngestionService.processBatch(batch);
                batchFutures.add(batchFuture);
            }
            
            // Wait for all batches to complete
            CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    int processed = batchFutures.stream()
                        .mapToInt(future -> future.join())
                        .sum();
                    totalProcessed.set(processed);
                    logger.info("Completed processing {} documents", processed);
                })
                .join();
                
            return CompletableFuture.completedFuture(totalProcessed.get());
            
        } catch (Exception e) {
            logger.error("Error during document ingestion: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<Void> deleteDocuments(List<String> documentIds) {
        try {
            batchIngestionService.deleteBatch(documentIds);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Error during document deletion: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
} 