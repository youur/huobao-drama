package com.huobao.drama.application.service;

import com.huobao.drama.domain.model.VideoMerge;

public interface VideoMergeService {
    String mergeEpisodeVideos(Long episodeId);
    
    void processVideoMerge(String taskId, Long episodeId);
}
