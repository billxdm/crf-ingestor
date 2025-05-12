package com.ecfr.service;

import com.ecfr.model.Agency;
import com.ecfr.repository.AgencyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LocalAgencyService {
    
    private final AgencyRepository agencyRepository;

    public LocalAgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public Optional<Agency> getAgencyByCode(String code) {
        return agencyRepository.findByCode(code);
    }

    public Optional<Agency> getAgencyByName(String name) {
        return agencyRepository.findByName(name);
    }

    public List<Agency> getAgenciesByParent(String parentAgency) {
        return agencyRepository.findByParentAgency(parentAgency);
    }
} 