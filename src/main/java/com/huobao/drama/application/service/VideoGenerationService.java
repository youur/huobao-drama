package com.huobao.drama.application.service;

import com.huobao.drama.api.dto.GenerateVideoRequest;
import com.huobao.drama.domain.model.VideoGeneration;

public interface VideoGenerationService {
    VideoGeneration generateVideo(GenerateVideoRequest request);
    
    VideoGeneration generateVideoFromStoryboard(Long storyboardId);
    
    void processVideoGeneration(Long videoGenId);
}
