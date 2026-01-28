package com.huobao.drama.application.service;

import com.huobao.drama.domain.model.Storyboard;
import java.util.List;

public interface StoryboardService {
    String generateStoryboard(Long episodeId, String model);
    
    List<Storyboard> getStoryboardsByEpisode(Long episodeId);
    
    Storyboard updateStoryboard(Long id, Storyboard storyboard);
    
    void deleteStoryboard(Long id);
}
