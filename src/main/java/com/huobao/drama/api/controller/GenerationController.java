package com.huobao.drama.api.controller;

import com.huobao.drama.api.dto.GenerateImageRequest;
import com.huobao.drama.api.dto.GenerateVideoRequest;
import com.huobao.drama.application.service.ImageGenerationService;
import com.huobao.drama.application.service.VideoGenerationService;
import com.huobao.drama.common.response.ApiResponse;
import com.huobao.drama.domain.model.ImageGeneration;
import com.huobao.drama.domain.model.VideoGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generation")
@RequiredArgsConstructor
public class GenerationController {

    private final ImageGenerationService imageGenerationService;
    private final VideoGenerationService videoGenerationService;

    @PostMapping("/image")
    public ApiResponse<ImageGeneration> generateImage(@RequestBody GenerateImageRequest request) {
        return ApiResponse.success(imageGenerationService.generateImage(request));
    }

    @PostMapping("/video")
    public ApiResponse<VideoGeneration> generateVideo(@RequestBody GenerateVideoRequest request) {
        return ApiResponse.success(videoGenerationService.generateVideo(request));
    }

    @PostMapping("/storyboards/{storyboardId}/video")
    public ApiResponse<VideoGeneration> generateVideoFromStoryboard(@PathVariable Long storyboardId) {
        return ApiResponse.success(videoGenerationService.generateVideoFromStoryboard(storyboardId));
    }
}
