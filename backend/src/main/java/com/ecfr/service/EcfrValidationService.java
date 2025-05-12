package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.dto.ecfrxml.EcfrbrwsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EcfrValidationService {
    private static final Logger log = LoggerFactory.getLogger(EcfrValidationService.class);

    public boolean validateEcfrData(EcfrDTO ecfrData) {
        if (ecfrData == null) {
            log.error("ECFR data is null");
            return false;
        }

        // Validate header
        if (ecfrData.getHeader() == null) {
            log.error("Header is missing");
            return false;
        }

        // Validate text and body
        if (ecfrData.getText() == null || ecfrData.getText().getBody() == null) {
            log.error("Text or body is missing");
            return false;
        }

        // Validate ECFR browse info
        EcfrbrwsDTO ecfrbrws = ecfrData.getText().getBody().getEcfrbrws();
        if (ecfrbrws == null) {
            log.error("ECFR browse info is missing");
            return false;
        }

        // Validate required fields
        if (ecfrbrws.getTitle() == null || ecfrbrws.getSubtitle() == null || 
            ecfrbrws.getChapter() == null || ecfrbrws.getPart() == null) {
            log.error("Missing required fields in ECFR browse info");
            return false;
        }

        log.info("ECFR data validation successful");
        return true;
    }
} 