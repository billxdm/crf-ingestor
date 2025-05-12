package com.ecfr.service;

import com.ecfr.dto.TitleDTO;
import com.ecfr.repository.TitleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TitleService {
    private static final Logger logger = LoggerFactory.getLogger(TitleService.class);

    @Autowired
    private TitleRepository titleRepository;

    public TitleDTO saveTitle(TitleDTO title) {
        title.setCreatedAt(LocalDateTime.now());
        title.setUpdatedAt(LocalDateTime.now());
        return titleRepository.save(title);
    }

    public Optional<TitleDTO> findByTitleNumber(String titleNumber) {
        return titleRepository.findByTitleNumber(titleNumber);
    }

    public List<TitleDTO> findAllTitles() {
        return titleRepository.findAll();
    }

    public void deleteTitle(String id) {
        titleRepository.deleteById(id);
    }

    public TitleDTO updateTitle(TitleDTO title) {
        if (title.getId() == null) {
            throw new IllegalArgumentException("Title ID cannot be null for update operation");
        }
        title.setUpdatedAt(LocalDateTime.now());
        return titleRepository.save(title);
    }
} 