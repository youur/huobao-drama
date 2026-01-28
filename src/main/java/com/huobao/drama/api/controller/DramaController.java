package com.huobao.drama.api.controller;

import com.huobao.drama.api.dto.DramaDTO;
import com.huobao.drama.application.service.DramaService;
import com.huobao.drama.common.response.ApiResponse;
import com.huobao.drama.domain.model.Drama;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import com.huobao.drama.domain.model.DramaCharacter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dramas")
@RequiredArgsConstructor
public class DramaController {

    private final DramaService dramaService;

    @PostMapping
    public ApiResponse<Drama> createDrama(@Valid @RequestBody DramaDTO dramaDTO) {
        return ApiResponse.success(dramaService.createDrama(dramaDTO));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(dramaService.getStats());
    }

    @GetMapping("/{id}")
    public ApiResponse<Drama> getDrama(@PathVariable Long id) {
        return ApiResponse.success(dramaService.getDrama(id));
    }

    @PutMapping("/{id}/outline")
    public ApiResponse<Drama> saveOutline(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(dramaService.saveOutline(id, data));
    }

    @GetMapping("/{id}/characters")
    public ApiResponse<List<DramaCharacter>> getCharacters(@PathVariable Long id) {
        return ApiResponse.success(dramaService.getCharacters(id));
    }

    @PutMapping("/{id}/characters")
    public ApiResponse<Void> saveCharacters(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List<Map<String, Object>> characters = (List<Map<String, Object>>) body.get("characters");
        dramaService.saveCharacters(id, characters);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/progress")
    public ApiResponse<Void> saveProgress(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String currentStep = (String) body.get("current_step");
        String stepData = body.get("step_data") != null ? body.get("step_data").toString() : null;
        dramaService.saveProgress(id, currentStep, stepData);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> listDramas(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int page_size) {
        
        // 修正排序字段为 id，确保最基础的查询能够跑通
        Pageable pageable = PageRequest.of(page - 1, Math.min(page_size, 200), Sort.by("id").descending());
        
        Page<Drama> dramaPage = dramaService.listDramas(status, genre, keyword, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", dramaPage.getContent());
        
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", page);
        pagination.put("page_size", page_size);
        pagination.put("total", dramaPage.getTotalElements());
        pagination.put("total_pages", dramaPage.getTotalPages());
        
        result.put("pagination", pagination);
        
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Drama> updateDrama(@PathVariable Long id, @RequestBody DramaDTO dramaDTO) {
        return ApiResponse.success(dramaService.updateDrama(id, dramaDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDrama(@PathVariable Long id) {
        dramaService.deleteDrama(id);
        return ApiResponse.success(null);
    }
}
