package com.huobao.drama.application.service;

import com.huobao.drama.api.dto.GenerateImageRequest;
import com.huobao.drama.domain.model.ImageGeneration;

public interface ImageGenerationService {
    ImageGeneration generateImage(GenerateImageRequest request);
    
    void processImageGeneration(Long imageGenId);
}
