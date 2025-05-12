package com.ecfr.service;

import com.ecfr.model.EcfrDocument;
import com.ecfr.repository.EcfrDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BatchIngestionService {
    private static final Logger logger = LoggerFactory.getLogger(BatchIngestionService.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EcfrDocumentRepository documentRepository;

    @Async
    public CompletableFuture<Integer> processBatch(List<EcfrDocument> documents) {
        AtomicInteger processedCount = new AtomicInteger(0);
        
        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, EcfrDocument.class);
            
            for (EcfrDocument document : documents) {
                Query query = new Query();
                query.addCriteria(org.springframework.data.mongodb.core.query.Criteria
                    .where("titleNumber").is(document.getTitleNumber())
                    .and("partNumber").is(document.getPartNumber())
                    .and("sectionNumber").is(document.getSectionNumber()));

                Update update = new Update()
                    .set("titleNumber", document.getTitleNumber())
                    .set("chapterNumber", document.getChapterNumber())
                    .set("partNumber", document.getPartNumber())
                    .set("sectionNumber", document.getSectionNumber())
                    .set("agency", document.getAgency())
                    .set("sectionHeading", document.getSectionHeading())
                    .set("fullText", document.getFullText())
                    .set("startDate", document.getStartDate())
                    .set("endDate", document.getEndDate())
                    .set("changeTypes", document.getChangeTypes())
                    .set("wordCount", document.getWordCount())
                    .set("checksum", document.getChecksum())
                    .set("structureIndex", document.getStructureIndex())
                    .set("createdAt", document.getCreatedAt())
                    .set("updatedAt", document.getUpdatedAt());

                bulkOps.upsert(query, update);
                
                if (processedCount.incrementAndGet() % BATCH_SIZE == 0) {
                    bulkOps.execute();
                    bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, EcfrDocument.class);
                    logger.info("Processed {} documents", processedCount.get());
                }
            }
            
            // Execute any remaining operations
            if (processedCount.get() % BATCH_SIZE != 0) {
                bulkOps.execute();
            }
            
            logger.info("Completed batch processing of {} documents", processedCount.get());
            return CompletableFuture.completedFuture(processedCount.get());
            
        } catch (Exception e) {
            logger.error("Error processing batch: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public void deleteBatch(List<String> documentIds) {
        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, EcfrDocument.class);
            
            for (String documentId : documentIds) {
                Query query = new Query();
                query.addCriteria(org.springframework.data.mongodb.core.query.Criteria
                    .where("_id").is(documentId));
                bulkOps.remove(query);
            }
            
            bulkOps.execute();
            logger.info("Deleted {} documents", documentIds.size());
            
        } catch (Exception e) {
            logger.error("Error deleting batch: {}", e.getMessage(), e);
            throw e;
        }
    }
} 