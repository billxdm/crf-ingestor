package com.ecfr.service;

import com.ecfr.dto.AgencyDTO;
import com.ecfr.exception.ECfrApiException;
import com.ecfr.model.Agency;
import com.ecfr.repository.AgencyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ECfrDataSyncService {
    private static final Logger logger = LoggerFactory.getLogger(ECfrDataSyncService.class);
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int retryAttempts;
    private final long retryDelay;
    private final AgencyRepository agencyRepository;

    public ECfrDataSyncService(
            RestTemplate eCfrRestTemplate,
            @Value("${ecfr.api.base-url}") String baseUrl,
            @Value("${ecfr.api.retry-attempts:3}") int retryAttempts,
            @Value("${ecfr.api.retry-delay:1000}") long retryDelay,
            AgencyRepository agencyRepository) {
        this.restTemplate = eCfrRestTemplate;
        this.baseUrl = baseUrl;
        this.retryAttempts = retryAttempts;
        this.retryDelay = retryDelay;
        this.agencyRepository = agencyRepository;
    }

    public void syncAgencies() {
        logger.info("Starting agency sync from eCFR API");
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/v1/agencies")
                    .build()
                    .toUriString();

            HttpHeaders headers = createHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<AgencyDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AgencyDTO[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Agency> agencies = Arrays.stream(response.getBody())
                    .map(this::mapToAgency)
                    .collect(Collectors.toList());
                
                agencyRepository.saveAll(agencies);
                logger.info("Completed agency sync. Synced {} agencies", agencies.size());
            } else {
                logger.warn("No agencies received from eCFR API. Status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to sync agencies from eCFR API", e);
            throw new ECfrApiException("Failed to sync agencies: " + e.getMessage(), e);
        }
    }

    private Agency mapToAgency(AgencyDTO dto) {
        Agency agency = new Agency();
        agency.setName(dto.getName());
        agency.setShortName(dto.getShortName());
        agency.setDisplayName(dto.getDisplayName());
        agency.setDescription(dto.getDescription());
        agency.setWebsite(dto.getWebsite());
        agency.setParentAgency(dto.getParentAgency());
        agency.setSortableName(dto.getSortableName());
        agency.setSlug(dto.getSlug());
        agency.setCfrReferences(dto.getCfrReferences().stream()
            .map(ref -> {
                Agency.CfrReference cfrRef = new Agency.CfrReference();
                cfrRef.setTitle(ref.getTitle());
                cfrRef.setChapter(ref.getChapter());
                return cfrRef;
            })
            .collect(Collectors.toList()));
        agency.setLastUpdated(LocalDateTime.now());
        return agency;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
} 