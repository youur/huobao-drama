package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.api.dto.GenerateImageRequest;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.application.service.AIService;
import com.huobao.drama.application.service.ImageGenerationService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.AIServiceConfig;
import com.huobao.drama.domain.model.ImageGeneration;
import com.huobao.drama.domain.model.Storyboard;
import com.huobao.drama.domain.repository.ImageGenerationRepository;
import com.huobao.drama.domain.repository.StoryboardRepository;
import com.huobao.drama.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationServiceImpl implements ImageGenerationService {

    private final ImageGenerationRepository imageGenerationRepository;
    private final StoryboardRepository storyboardRepository;
    private final AIService aiService;
    private final AIConfigService aiConfigService;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ImageGeneration generateImage(GenerateImageRequest request) {
        ImageGeneration imageGen = new ImageGeneration();
        imageGen.setStoryboardId(request.getStoryboardId());
        imageGen.setDramaId(request.getDramaId());
        imageGen.setProvider(request.getProvider() != null ? request.getProvider() : "openai");
        imageGen.setPrompt(request.getPrompt());
        imageGen.setNegativePrompt(request.getNegativePrompt());
        imageGen.setModel(request.getModel());
        imageGen.setSize(request.getSize());
        imageGen.setQuality(request.getQuality());
        imageGen.setStyle(request.getStyle());
        imageGen.setSteps(request.getSteps());
        imageGen.setCfgScale(request.getCfgScale());
        imageGen.setSeed(request.getSeed());
        imageGen.setWidth(request.getWidth());
        imageGen.setHeight(request.getHeight());
        imageGen.setStatus("pending");

        if (request.getReferenceImages() != null) {
            try {
                imageGen.setReferenceImages(objectMapper.writeValueAsString(request.getReferenceImages()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize reference images", e);
            }
        }

        imageGen = imageGenerationRepository.save(imageGen);

        // 异步执行
        processImageGeneration(imageGen.getId());

        return imageGen;
    }

    @Async("taskExecutor")
    @Override
    public void processImageGeneration(Long imageGenId) {
        ImageGeneration imageGen = imageGenerationRepository.findById(imageGenId).orElse(null);
        if (imageGen == null) return;

        try {
            imageGen.setStatus("processing");
            imageGenerationRepository.save(imageGen);

            // 获取 AI 配置
            List<AIServiceConfig> configs = aiConfigService.listConfigs("image");
            AIServiceConfig config = configs.stream()
                    .filter(c -> imageGen.getModel() == null || c.getModel().equals(imageGen.getModel()))
                    .findFirst()
                    .orElse(configs.isEmpty() ? null : configs.get(0));

            if (config == null) throw new RuntimeException("No image AI config found");

            log.info("Starting image generation for id: {}, prompt: {}", imageGenId, imageGen.getPrompt());

            // 调用 AI 生成图片 (目前 AIService 只返回 List<String>)
            List<String> imageUrls = aiService.generateImage(imageGen.getPrompt(), imageGen.getSize(), 1, 
                    config.getModel(), config.getBaseUrl(), config.getApiKey());

            if (imageUrls == null || imageUrls.isEmpty()) {
                throw new RuntimeException("AI generated no images");
            }

            String remoteUrl = imageUrls.get(0);
            
            // 下载到本地存储
            StorageService.DownloadResult downloadResult = storageService.downloadFromUrlWithPath(remoteUrl, "images");

            // 更新记录
            imageGen.setStatus("completed");
            imageGen.setImageUrl(remoteUrl);
            imageGen.setLocalPath(downloadResult.getRelativePath());
            imageGen.setCompletedAt(LocalDateTime.now());
            imageGenerationRepository.save(imageGen);

            // 同步更新分镜
            if (imageGen.getStoryboardId() != null) {
                storyboardRepository.findById(imageGen.getStoryboardId()).ifPresent(sb -> {
                    sb.setComposedImage(remoteUrl);
                    storyboardRepository.save(sb);
                });
            }

            log.info("Image generation completed for id: {}", imageGenId);

        } catch (Exception e) {
            log.error("Image generation failed for id: {}", imageGenId, e);
            imageGen.setStatus("failed");
            imageGen.setErrorMsg(e.getMessage());
            imageGenerationRepository.save(imageGen);
        }
    }
}
