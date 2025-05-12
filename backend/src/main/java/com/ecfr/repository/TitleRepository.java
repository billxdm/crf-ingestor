package com.ecfr.repository;

import com.ecfr.dto.TitleDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRepository extends MongoRepository<TitleDTO, String> {
    Optional<TitleDTO> findByTitleNumber(String titleNumber);
    boolean existsByTitleNumber(String titleNumber);
} 