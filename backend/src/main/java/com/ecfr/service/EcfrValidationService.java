package com.ecfr.service;

import com.ecfr.dto.ecfrxml.EcfrDTO;
import com.ecfr.dto.ecfrxml.DivisionDTO;
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

        // Validate divisions (DIV1 elements)
        if (ecfrData.getDivisions() == null || ecfrData.getDivisions().isEmpty()) {
            log.error("No DIV1 elements found");
            return false;
        }

        // Validate each division
        boolean foundTitle = false;
        for (DivisionDTO division : ecfrData.getDivisions()) {
            if (division == null) {
                log.error("Null division found");
                return false;
            }

            if ("TITLE".equals(division.getType())) {
                foundTitle = true;
                if (division.getNumber() == null || division.getNumber().trim().isEmpty()) {
                    log.error("Title division missing number");
                    return false;
                }
                if (division.getHead() == null || division.getHead().trim().isEmpty()) {
                    log.error("Title division missing heading");
                    return false;
                }
            }
        }

        if (!foundTitle) {
            log.error("No TITLE division found");
            return false;
        }

        // Validate title number can be extracted
        String titleNumber = ecfrData.getTitleNumber();
        if (titleNumber == null || titleNumber.trim().isEmpty()) {
            log.error("Could not determine title number from XML content");
            return false;
        }

        log.info("ECFR data validation successful for title {}", titleNumber);
        return true;
    }
} 