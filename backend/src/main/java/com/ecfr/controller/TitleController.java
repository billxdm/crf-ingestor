package com.ecfr.controller;

import com.ecfr.dto.TitleDTO;
import com.ecfr.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    @PostMapping
    public ResponseEntity<TitleDTO> createTitle(@RequestBody TitleDTO title) {
        return ResponseEntity.ok(titleService.saveTitle(title));
    }

    @GetMapping("/{titleNumber}")
    public ResponseEntity<TitleDTO> getTitle(@PathVariable String titleNumber) {
        return titleService.findByTitleNumber(titleNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TitleDTO>> getAllTitles() {
        return ResponseEntity.ok(titleService.findAllTitles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TitleDTO> updateTitle(@PathVariable String id, @RequestBody TitleDTO title) {
        title.setId(id);
        return ResponseEntity.ok(titleService.updateTitle(title));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable String id) {
        titleService.deleteTitle(id);
        return ResponseEntity.ok().build();
    }
} 