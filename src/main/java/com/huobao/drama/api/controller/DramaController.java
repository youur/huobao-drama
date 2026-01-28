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

@RestController
@RequestMapping("/api/dramas")
@RequiredArgsConstructor
public class DramaController {

    private final DramaService dramaService;

    @PostMapping
    public ApiResponse<Drama> createDrama(@Valid @RequestBody DramaDTO dramaDTO) {
        return ApiResponse.success(dramaService.createDrama(dramaDTO));
    }

    @GetMapping("/{id}")
    public ApiResponse<Drama> getDrama(@PathVariable Long id) {
        return ApiResponse.success(dramaService.getDrama(id));
    }

    @GetMapping
    public ApiResponse<Page<Drama>> listDramas(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        // Convert 1-based page to 0-based for Spring Data
        Pageable pageable = PageRequest.of(page - 1, Math.min(pageSize, 200), Sort.by("updatedAt").descending());
        return ApiResponse.success(dramaService.listDramas(status, genre, keyword, pageable));
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
