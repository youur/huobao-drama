package com.huobao.drama.application.service.impl;

import com.huobao.drama.api.dto.GenerateVideoRequest;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.application.service.VideoGenerationService;
import com.huobao.drama.domain.model.AIServiceConfig;
import com.huobao.drama.domain.model.VideoGeneration;
import com.huobao.drama.domain.repository.StoryboardRepository;
import com.huobao.drama.domain.repository.VideoGenerationRepository;
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
public class VideoGenerationServiceImpl implements VideoGenerationService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoryboardRepository storyboardRepository;
    private final AIConfigService aiConfigService;
    private final StorageService storageService;

    @Override
    @Transactional
    public VideoGeneration generateVideoFromStoryboard(Long storyboardId) {
        Storyboard sb = storyboardRepository.findById(storyboardId)
                .orElseThrow(() -> new BusinessException(404, "Storyboard not found"));
        
        GenerateVideoRequest request = new GenerateVideoRequest();
        request.setStoryboardId(storyboardId);
        request.setDramaId(sb.getEpisodeId()); // 简化处理，实际上应获取DramaID
        request.setPrompt(sb.getVideoPrompt());
        request.setImageUrl(sb.getComposedImage());
        request.setDuration(sb.getDuration());
        
        return generateVideo(request);
    }

    @Override
    @Transactional
    public VideoGeneration generateVideo(GenerateVideoRequest request) {
        VideoGeneration videoGen = new VideoGeneration();
        videoGen.setStoryboardId(request.getStoryboardId());
        videoGen.setDramaId(request.getDramaId());
        videoGen.setImageGenId(request.getImageGenId());
        videoGen.setProvider(request.getProvider() != null ? request.getProvider() : "doubao");
        videoGen.setPrompt(request.getPrompt());
        videoGen.setModel(request.getModel());
        videoGen.setDuration(request.getDuration());
        videoGen.setFps(request.getFps());
        videoGen.setAspectRatio(request.getAspectRatio());
        videoGen.setStyle(request.getStyle());
        videoGen.setMotionLevel(request.getMotionLevel());
        videoGen.setCameraMotion(request.getCameraMotion());
        videoGen.setSeed(request.getSeed());
        videoGen.setStatus("pending");

        videoGen = videoGenerationRepository.save(videoGen);

        // 异步执行
        processVideoGeneration(videoGen.getId());

        return videoGen;
    }

    @Async("taskExecutor")
    @Override
    public void processVideoGeneration(Long videoGenId) {
        VideoGeneration videoGen = videoGenerationRepository.findById(videoGenId).orElse(null);
        if (videoGen == null) return;

        try {
            videoGen.setStatus("processing");
            videoGenerationRepository.save(videoGen);

            // 获取 AI 配置
            List<AIServiceConfig> configs = aiConfigService.listConfigs("video");
            AIServiceConfig config = configs.stream()
                    .filter(c -> videoGen.getModel() == null || c.getModel().equals(videoGen.getModel()))
                    .findFirst()
                    .orElse(configs.isEmpty() ? null : configs.get(0));

            if (config == null) throw new RuntimeException("No video AI config found");

            log.info("Starting video generation for id: {}, prompt: {}", videoGenId, videoGen.getPrompt());

            // TODO: 这里需要对接具体的视频 AI 客户端 (Feign)
            // 暂时模拟一个结果
            String mockVideoUrl = "http://example.com/mock.mp4";
            
            // 模拟延迟
            Thread.sleep(5000);

            // 下载到本地
            // StorageService.DownloadResult downloadResult = storageService.downloadFromUrlWithPath(mockVideoUrl, "videos");

            // 更新记录
            videoGen.setStatus("completed");
            videoGen.setVideoUrl(mockVideoUrl);
            videoGen.setCompletedAt(LocalDateTime.now());
            videoGenerationRepository.save(videoGen);

            // 同步更新分镜
            if (videoGen.getStoryboardId() != null) {
                storyboardRepository.findById(videoGen.getStoryboardId()).ifPresent(sb -> {
                    sb.setVideoUrl(mockVideoUrl);
                    sb.setStatus("completed");
                    storyboardRepository.save(sb);
                });
            }

            log.info("Video generation completed for id: {}", videoGenId);

        } catch (Exception e) {
            log.error("Video generation failed for id: {}", videoGenId, e);
            videoGen.setStatus("failed");
            videoGen.setErrorMsg(e.getMessage());
            videoGenerationRepository.save(videoGen);
        }
    }
}
