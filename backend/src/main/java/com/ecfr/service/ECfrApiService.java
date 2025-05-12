package com.ecfr.service;

import com.ecfr.dto.AgencyDTO;
import com.ecfr.exception.ECfrApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ECfrApiService {
    private static final Logger logger = LoggerFactory.getLogger(ECfrApiService.class);
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final int retryAttempts;
    private final long retryDelay;

    public ECfrApiService(
            RestTemplate eCfrRestTemplate,
            @Value("${ecfr.api.base-url}") String baseUrl,
            @Value("${ecfr.api.retry-attempts:3}") int retryAttempts,
            @Value("${ecfr.api.retry-delay:1000}") long retryDelay) {
        this.restTemplate = eCfrRestTemplate;
        this.baseUrl = baseUrl;
        this.retryAttempts = retryAttempts;
        this.retryDelay = retryDelay;
    }

    public List<AgencyDTO> getAllAgencies() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/admin/v1/agencies.json")
                .build()
                .toUriString();

        return executeWithRetry(() -> {
            HttpHeaders headers = createHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<AgencyResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AgencyResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getAgencies();
            }
            throw new ECfrApiException("Failed to retrieve agencies: " + response.getStatusCode());
        });
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Add any required headers for eCFR API
        return headers;
    }

    private <T> T executeWithRetry(ECfrApiCallable<T> callable) {
        int attempts = 0;
        while (attempts < retryAttempts) {
            try {
                return callable.call();
            } catch (Exception e) {
                attempts++;
                if (attempts == retryAttempts) {
                    logger.error("Failed after {} attempts", retryAttempts, e);
                    throw new ECfrApiException("API call failed after " + retryAttempts + " attempts", e);
                }
                logger.warn("Attempt {} failed, retrying in {} ms", attempts, retryDelay);
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ECfrApiException("Retry interrupted", ie);
                }
            }
        }
        throw new ECfrApiException("Unexpected error in retry logic");
    }

    @FunctionalInterface
    private interface ECfrApiCallable<T> {
        T call();
    }

    // Wrapper class for the API response
    private static class AgencyResponse {
        private List<AgencyDTO> agencies;

        public List<AgencyDTO> getAgencies() {
            return agencies;
        }

        public void setAgencies(List<AgencyDTO> agencies) {
            this.agencies = agencies;
        }
    }
} 