package com.huobao.drama.application.service.impl;

import com.huobao.drama.api.dto.GenerateVideoRequest;
import com.huobao.drama.application.service.AsyncProcessor;
import com.huobao.drama.application.service.VideoGenerationService;
import com.huobao.drama.domain.model.Storyboard;
import com.huobao.drama.domain.model.VideoGeneration;
import com.huobao.drama.domain.repository.StoryboardRepository;
import com.huobao.drama.domain.repository.VideoGenerationRepository;
import com.huobao.drama.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoGenerationServiceImpl implements VideoGenerationService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoryboardRepository storyboardRepository;
    private final AsyncProcessor asyncProcessor;

    @Override
    @Transactional
    public VideoGeneration generateVideo(GenerateVideoRequest request) {
        VideoGeneration videoGen = new VideoGeneration();
        videoGen.setStoryboardId(request.getStoryboardId());
        videoGen.setDramaId(request.getDramaId());
        videoGen.setPrompt(request.getPrompt());
        videoGen.setStatus("pending");
        
        videoGen = videoGenerationRepository.save(videoGen);
        asyncProcessor.processVideoGeneration(videoGen.getId());
        return videoGen;
    }

    @Override
    @Transactional
    public VideoGeneration generateVideoFromStoryboard(Long storyboardId) {
        Storyboard sb = storyboardRepository.findById(storyboardId)
                .orElseThrow(() -> new BusinessException(404, "Storyboard not found"));
        
        GenerateVideoRequest req = new GenerateVideoRequest();
        req.setStoryboardId(storyboardId);
        req.setPrompt(sb.getVideoPrompt());
        req.setImageUrl(sb.getComposedImage());
        req.setDuration(sb.getDuration());
        req.setDramaId(0L); // 需要实际的DramaID
        
        return generateVideo(req);
    }

    @Override
    public void processVideoGeneration(Long videoGenId) {
        asyncProcessor.processVideoGeneration(videoGenId);
    }
}
