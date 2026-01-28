package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.api.dto.GenerateImageRequest;
import com.huobao.drama.application.service.AsyncProcessor;
import com.huobao.drama.application.service.ImageGenerationService;
import com.huobao.drama.domain.model.ImageGeneration;
import com.huobao.drama.domain.repository.ImageGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationServiceImpl implements ImageGenerationService {

    private final ImageGenerationRepository imageGenerationRepository;
    private final AsyncProcessor asyncProcessor;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ImageGeneration generateImage(GenerateImageRequest request) {
        ImageGeneration imageGen = new ImageGeneration();
        imageGen.setStoryboardId(request.getStoryboardId());
        imageGen.setDramaId(request.getDramaId());
        imageGen.setProvider(request.getProvider() != null ? request.getProvider() : "openai");
        imageGen.setPrompt(request.getPrompt());
        imageGen.setModel(request.getModel());
        imageGen.setSize(request.getSize());
        imageGen.setStatus("pending");

        if (request.getReferenceImages() != null) {
            try {
                imageGen.setReferenceImages(objectMapper.writeValueAsString(request.getReferenceImages()));
            } catch (JsonProcessingException e) {
                log.error("Reference images serialization failed", e);
            }
        }

        imageGen = imageGenerationRepository.save(imageGen);
        asyncProcessor.processImageGeneration(imageGen.getId());
        return imageGen;
    }

    @Override
    public void processImageGeneration(Long imageGenId) {
        asyncProcessor.processImageGeneration(imageGenId);
    }
}
