package com.huobao.drama.application.service;

import com.huobao.drama.domain.model.Storyboard;

public interface PromptOrchestrator {
    String generateImagePrompt(Storyboard storyboard);
    String generateVideoPrompt(Storyboard storyboard);
}
