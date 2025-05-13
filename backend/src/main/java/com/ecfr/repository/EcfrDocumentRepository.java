package com.ecfr.repository;

import com.ecfr.model.EcfrDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EcfrDocumentRepository extends MongoRepository<EcfrDocument, String> {
    Optional<EcfrDocument> findByTitleNumberAndPartNumberAndSectionNumber(String titleNumber, String partNumber, String sectionNumber);
    boolean existsByTitleNumberAndPartNumberAndSectionNumber(String titleNumber, String partNumber, String sectionNumber);
    List<EcfrDocument> findByTitleNumber(String titleNumber);
} 