package com.huobao.drama.application.service.impl;

import com.huobao.drama.application.service.AsyncProcessor;
import com.huobao.drama.application.service.StoryboardService;
import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.DramaCharacter;
import com.huobao.drama.domain.model.Episode;
import com.huobao.drama.domain.model.Scene;
import com.huobao.drama.domain.model.Storyboard;
import com.huobao.drama.domain.repository.DramaCharacterRepository;
import com.huobao.drama.domain.repository.EpisodeRepository;
import com.huobao.drama.domain.repository.SceneRepository;
import com.huobao.drama.domain.repository.StoryboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryboardServiceImpl implements StoryboardService {

    private final EpisodeRepository episodeRepository;
    private final DramaCharacterRepository characterRepository;
    private final SceneRepository sceneRepository;
    private final StoryboardRepository storyboardRepository;
    private final TaskService taskService;
    private final AsyncProcessor asyncProcessor;

    @Override
    @Transactional
    public String generateStoryboard(Long episodeId, String model) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new BusinessException(404, "Episode not found"));

        String scriptContent = episode.getScriptContent() != null ? episode.getScriptContent() : episode.getDescription();
        if (scriptContent == null || scriptContent.isEmpty()) throw new BusinessException(400, "Script is empty");

        List<DramaCharacter> characters = characterRepository.findByDramaId(episode.getDramaId());
        String charList = characters.stream().map(c -> c.getName()).collect(Collectors.joining(", "));

        String prompt = "Please break down the script into storyboard JSON: " + scriptContent + "\nCharacters: " + charList;

        String taskId = taskService.createTask("storyboard_generation", episodeId.toString()).getId();
        asyncProcessor.processStoryboardGeneration(taskId, episodeId, model, prompt);
        return taskId;
    }

    @Override
    public List<Storyboard> getStoryboardsByEpisode(Long episodeId) {
        return storyboardRepository.findByEpisodeIdOrderByStoryboardNumberAsc(episodeId);
    }

    @Override
    @Transactional
    public Storyboard updateStoryboard(Long id, Storyboard sbDto) {
        Storyboard sb = storyboardRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Not found"));
        if (sbDto.getAction() != null) sb.setAction(sbDto.getAction());
        return storyboardRepository.save(sb);
    }

    @Override
    public void deleteStoryboard(Long id) {
        storyboardRepository.deleteById(id);
    }
}
