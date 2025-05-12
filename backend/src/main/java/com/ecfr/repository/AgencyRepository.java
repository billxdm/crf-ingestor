package com.ecfr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecfr.model.Agency;

public interface AgencyRepository extends MongoRepository<Agency, String> {
    Optional<Agency> findByCode(String code);
    Optional<Agency> findByName(String name);
    List<Agency> findByParentAgency(String parentAgency);
} 