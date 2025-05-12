package com.ecfr.repository;

import com.ecfr.model.Regulation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.time.LocalDateTime;

public interface RegulationRepository extends MongoRepository<Regulation, String> {
    List<Regulation> findByAgency(String agency);
    
    @Query(value = "{ 'agency': ?0 }", fields = "{ 'wordCount': 1, 'title': 1, 'partNumber': 1 }")
    List<Regulation> findWordCountsByAgency(String agency);
    
    List<Regulation> findByAgencyAndEffectiveDateBetween(String agency, LocalDateTime start, LocalDateTime end);
    
    @Query(value = "{ 'agency': ?0 }", fields = "{ 'complexityScore': 1 }")
    List<Regulation> findComplexityScoresByAgency(String agency);
} 