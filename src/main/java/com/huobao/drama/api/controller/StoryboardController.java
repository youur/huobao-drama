package com.huobao.drama.api.controller;

import com.huobao.drama.application.service.StoryboardService;
import com.huobao.drama.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.huobao.drama.domain.model.Storyboard;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoryboardController {

    private final StoryboardService storyboardService;

    @PostMapping("/episodes/{episodeId}/storyboards/generate")
    public ApiResponse<String> generateStoryboard(
            @PathVariable Long episodeId,
            @RequestParam(required = false) String model) {
        String taskId = storyboardService.generateStoryboard(episodeId, model);
        return ApiResponse.success(taskId);
    }

    @GetMapping("/episodes/{episodeId}/storyboards")
    public ApiResponse<List<Storyboard>> getStoryboards(@PathVariable Long episodeId) {
        return ApiResponse.success(storyboardService.getStoryboardsByEpisode(episodeId));
    }

    @PutMapping("/storyboards/{id}")
    public ApiResponse<Storyboard> updateStoryboard(@PathVariable Long id, @RequestBody Storyboard storyboard) {
        return ApiResponse.success(storyboardService.updateStoryboard(id, storyboard));
    }

    @DeleteMapping("/storyboards/{id}")
    public ApiResponse<Void> deleteStoryboard(@PathVariable Long id) {
        storyboardService.deleteStoryboard(id);
        return ApiResponse.success(null);
    }
}
