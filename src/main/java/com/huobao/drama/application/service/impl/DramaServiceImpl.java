package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.api.dto.DramaDTO;
import com.huobao.drama.application.service.DramaService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.Drama;
import com.huobao.drama.domain.repository.DramaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DramaServiceImpl implements DramaService {

    private final DramaRepository dramaRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Drama createDrama(DramaDTO dto) {
        Drama drama = new Drama();
        drama.setTitle(dto.getTitle());
        drama.setDescription(dto.getDescription());
        drama.setGenre(dto.getGenre());
        drama.setStyle(dto.getStyle() != null ? dto.getStyle() : "realistic");
        drama.setStatus("draft");
        
        if (dto.getTags() != null) {
            try {
                drama.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize tags", e);
            }
        }

        return dramaRepository.save(drama);
    }

    @Override
    public Drama getDrama(Long id) {
        return dramaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Drama not found"));
    }

    @Override
    public Page<Drama> listDramas(String status, String genre, String keyword, Pageable pageable) {
        Specification<Drama> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (genre != null && !genre.isEmpty()) {
                predicates.add(cb.equal(root.get("genre"), genre));
            }
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("description"), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return dramaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public Drama updateDrama(Long id, DramaDTO dto) {
        Drama drama = getDrama(id);
        
        if (dto.getTitle() != null) drama.setTitle(dto.getTitle());
        if (dto.getDescription() != null) drama.setDescription(dto.getDescription());
        if (dto.getGenre() != null) drama.setGenre(dto.getGenre());
        if (dto.getStyle() != null) drama.setStyle(dto.getStyle());
        if (dto.getStatus() != null) drama.setStatus(dto.getStatus());
        
        if (dto.getTags() != null) {
            try {
                drama.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize tags", e);
            }
        }

        return dramaRepository.save(drama);
    }

    @Override
    @Transactional
    public void deleteDrama(Long id) {
        Drama drama = getDrama(id);
        drama.setDeletedAt(java.time.LocalDateTime.now());
        dramaRepository.save(drama);
    }
}
