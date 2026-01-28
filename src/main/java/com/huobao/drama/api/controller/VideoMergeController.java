package com.huobao.drama.api.controller;

import com.huobao.drama.application.service.VideoMergeService;
import com.huobao.drama.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/episodes")
@RequiredArgsConstructor
public class VideoMergeController {

    private final VideoMergeService videoMergeService;

    @PostMapping("/{episodeId}/merge")
    public ApiResponse<String> mergeVideos(@PathVariable Long episodeId) {
        String taskId = videoMergeService.mergeEpisodeVideos(episodeId);
        return ApiResponse.success(taskId);
    }
}
