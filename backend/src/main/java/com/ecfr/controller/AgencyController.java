package com.ecfr.controller;

import com.ecfr.model.Agency;
import com.ecfr.service.ECfrDataSyncService;
import com.ecfr.service.LocalAgencyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agencies")
public class AgencyController {

    private final LocalAgencyService localAgencyService;
    private final ECfrDataSyncService eCfrDataSyncService;

    public AgencyController(
            LocalAgencyService localAgencyService,
            ECfrDataSyncService eCfrDataSyncService) {
        this.localAgencyService = localAgencyService;
        this.eCfrDataSyncService = eCfrDataSyncService;
    }

    @GetMapping
    public List<Agency> getAllAgencies() {
        return localAgencyService.getAllAgencies();
    }

    @GetMapping("/{code}")
    public Optional<Agency> getAgencyByCode(@PathVariable String code) {
        return localAgencyService.getAgencyByCode(code);
    }

    @GetMapping("/name/{name}")
    public Optional<Agency> getAgencyByName(@PathVariable String name) {
        return localAgencyService.getAgencyByName(name);
    }

    @GetMapping("/parent/{parentAgency}")
    public List<Agency> getAgenciesByParent(@PathVariable String parentAgency) {
        return localAgencyService.getAgenciesByParent(parentAgency);
    }

    @PostMapping("/sync")
    public void syncAgencies() {
        eCfrDataSyncService.syncAgencies();
    }
} 