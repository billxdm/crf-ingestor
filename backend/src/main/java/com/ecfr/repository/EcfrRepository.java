package com.ecfr.repository;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EcfrRepository extends MongoRepository<EcfrDTO, String> {
    // Find by title number
    @Query(value = "{ 'text.body.ecfrbrws.title': ?0 }")
    Optional<EcfrDTO> findByTitleNumber(String titleNumber);

    // Find by effective date
    @Query(value = "{ 'header.effectiveDate': ?0 }")
    List<EcfrDTO> findByEffectiveDate(String effectiveDate);

    // Find by title number and effective date
    @Query(value = "{ 'text.body.ecfrbrws.title': ?0, 'header.effectiveDate': ?1 }")
    Optional<EcfrDTO> findByTitleNumberAndEffectiveDate(String titleNumber, String effectiveDate);

    // Find by division type
    @Query(value = "{ 'divisions.type': ?0 }")
    List<EcfrDTO> findByDivisionType(String type);

    // Find by division number
    @Query(value = "{ 'divisions.number': ?0 }")
    List<EcfrDTO> findByDivisionNumber(String number);

    // Find by division heading
    @Query(value = "{ 'divisions.head': { $regex: ?0, $options: 'i' } }")
    List<EcfrDTO> findByDivisionHeading(String heading);

    // Find by authority content
    @Query(value = "{ 'divisions.authority.content': { $regex: ?0, $options: 'i' } }")
    List<EcfrDTO> findByAuthorityContent(String content);

    // Find by source
    @Query(value = "{ 'divisions.authority.source': ?0 }")
    List<EcfrDTO> findBySource(String source);
} 