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

import com.huobao.drama.domain.model.DramaCharacter;
import com.huobao.drama.domain.repository.DramaCharacterRepository;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DramaServiceImpl implements DramaService {

    private final DramaRepository dramaRepository;
    private final DramaCharacterRepository characterRepository;
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
        try {
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
                return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
            };
            return dramaRepository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("Error listing dramas with filters: status={}, genre={}, keyword={}", status, genre, keyword, e);
            throw e;
        }
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

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_projects", dramaRepository.count());
        stats.put("total_images", 0);
        stats.put("total_videos", 0);
        stats.put("pending_tasks", 0);
        return stats;
    }

    @Override
    @Transactional
    public Drama saveOutline(Long id, Map<String, Object> data) {
        Drama drama = getDrama(id);
        if (data.containsKey("title")) drama.setTitle((String) data.get("title"));
        if (data.containsKey("description")) drama.setDescription((String) data.get("description"));
        if (data.containsKey("summary")) drama.setDescription((String) data.get("summary"));
        if (data.containsKey("genre")) drama.setGenre((String) data.get("genre"));
        
        if (data.containsKey("tags")) {
            try {
                drama.setTags(objectMapper.writeValueAsString(data.get("tags")));
            } catch (JsonProcessingException e) {
                log.error("Tags serialization failed", e);
            }
        }
        return dramaRepository.save(drama);
    }

    @Override
    public List<DramaCharacter> getCharacters(Long dramaId) {
        return characterRepository.findByDramaId(dramaId);
    }

    @Override
    @Transactional
    public void saveCharacters(Long id, List<Map<String, Object>> charactersData) {
        for (Map<String, Object> data : charactersData) {
            String name = (String) data.get("name");
            List<DramaCharacter> existing = characterRepository.findByDramaId(id);
            DramaCharacter character = existing.stream()
                    .filter(c -> c.getName().equals(name))
                    .findFirst().orElse(new DramaCharacter());
            
            character.setDramaId(id);
            character.setName(name);
            character.setRole((String) data.get("role"));
            character.setDescription((String) data.get("description"));
            characterRepository.save(character);
        }
    }

    @Override
    @Transactional
    public void saveProgress(Long id, String currentStep, String stepData) {
        Drama drama = getDrama(id);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("current_step", currentStep);
        metadata.put("step_data", stepData);
        try {
            drama.setMetadata(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            log.error("Progress serialization failed", e);
        }
        dramaRepository.save(drama);
    }
}
