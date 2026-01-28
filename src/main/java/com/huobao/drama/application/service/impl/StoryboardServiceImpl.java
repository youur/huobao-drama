package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.api.dto.StoryboardAIResult;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.application.service.AIService;
import com.huobao.drama.application.service.StoryboardService;
import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.AIServiceConfig;
import com.huobao.drama.domain.model.DramaCharacter;
import com.huobao.drama.domain.model.Episode;
import com.huobao.drama.domain.model.Scene;
import com.huobao.drama.domain.model.Storyboard;
import com.huobao.drama.domain.repository.DramaCharacterRepository;
import com.huobao.drama.domain.repository.EpisodeRepository;
import com.huobao.drama.domain.repository.SceneRepository;
import com.huobao.drama.domain.repository.StoryboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryboardServiceImpl implements StoryboardService {

    private final EpisodeRepository episodeRepository;
    private final DramaCharacterRepository characterRepository;
    private final SceneRepository sceneRepository;
    private final StoryboardRepository storyboardRepository;
    private final AIService aiService;
    private final AIConfigService aiConfigService;
    private final TaskService taskService;
    private final PromptOrchestrator promptOrchestrator;
    private final ObjectMapper objectMapper;

    @Override
    public String generateStoryboard(Long episodeId, String model) {
// ... 保持原有逻辑 ...
            sb.setAtmosphere(item.getAtmosphere());
            sb.setCharacters(objectMapper.writeValueAsString(item.getCharacters()));
            
            // 使用 PromptOrchestrator 生成提示词
            sb.setImagePrompt(promptOrchestrator.generateImagePrompt(sb));
            sb.setVideoPrompt(promptOrchestrator.generateVideoPrompt(sb));
            
            sb.setStatus("pending");
            storyboardRepository.save(sb);
        }
    }
        if (scriptContent == null || scriptContent.isEmpty()) {
            throw new BusinessException(400, "剧本内容为空");
        }

        // 获取角色和场景信息用于 Prompt
        List<DramaCharacter> characters = characterRepository.findByDramaId(episode.getDramaId());
        String characterList = characters.stream()
                .map(c -> String.format("{\"id\": %d, \"name\": \"%s\"}", c.getId(), c.getName()))
                .collect(Collectors.joining(", ", "[", "]"));

        List<Scene> scenes = sceneRepository.findByDramaId(episode.getDramaId());
        String sceneList = scenes.stream()
                .map(s -> String.format("{\"id\": %d, \"location\": \"%s\", \"time\": \"%s\"}", s.getId(), s.getLocation(), s.getTime()))
                .collect(Collectors.joining(", ", "[", "]"));

        String prompt = buildPrompt(scriptContent, characterList, sceneList);

        // 创建异步任务
        String taskId = taskService.createTask("storyboard_generation", episodeId.toString()).getId();

        // 异步执行生成
        processStoryboardGeneration(taskId, episodeId, model, prompt);

        return taskId;
    }

    @Async("taskExecutor")
    public void processStoryboardGeneration(String taskId, Long episodeId, String model, String prompt) {
        try {
            taskService.updateTaskStatus(taskId, "processing", 10, "开始生成分镜头...");

            // 获取默认或指定的 AI 配置
            List<AIServiceConfig> configs = aiConfigService.listConfigs("text");
            AIServiceConfig config = configs.stream()
                    .filter(c -> model == null || model.isEmpty() || c.getModel().equals(model))
                    .findFirst()
                    .orElse(configs.isEmpty() ? null : configs.get(0));

            if (config == null) {
                throw new RuntimeException("No suitable AI configuration found");
            }

            String aiResponse = aiService.generateText(prompt, "你是一个专业的影视编剧和分镜师", config.getModel(), 
                    config.getBaseUrl(), config.getApiKey(), config.getEndpoint());

            taskService.updateTaskStatus(taskId, "processing", 50, "分镜头生成完成，正在解析结果...");

            // 解析 JSON
            String jsonPart = extractJson(aiResponse);
            StoryboardAIResult result = objectMapper.readValue(jsonPart, StoryboardAIResult.class);

            taskService.updateTaskStatus(taskId, "processing", 70, "正在保存分镜头...");
            saveStoryboards(episodeId, result.getStoryboards());

            // 更新剧集总时长
            int totalDuration = result.getStoryboards().stream().mapToInt(StoryboardAIResult.StoryboardItem::getDuration).sum();
            int durationMinutes = (totalDuration + 59) / 60;
            
            Episode episode = episodeRepository.findById(episodeId).get();
            episode.setDuration(durationMinutes);
            episodeRepository.save(episode);

            taskService.updateTaskResult(taskId, result);
            log.info("Storyboard generation completed for episode: {}", episodeId);

        } catch (Exception e) {
            log.error("Failed to process storyboard generation", e);
            taskService.updateTaskError(taskId, e);
        }
    }

    @Override
    public List<Storyboard> getStoryboardsByEpisode(Long episodeId) {
        return storyboardRepository.findByEpisodeIdOrderByStoryboardNumberAsc(episodeId);
    }

    @Override
    @Transactional
    public Storyboard updateStoryboard(Long id, Storyboard sbDto) {
        Storyboard sb = storyboardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Storyboard not found"));
        
        if (sbDto.getTitle() != null) sb.setTitle(sbDto.getTitle());
        if (sbDto.getAction() != null) sb.setAction(sbDto.getAction());
        if (sbDto.getDialogue() != null) sb.setDialogue(sbDto.getDialogue());
        if (sbDto.getAtmosphere() != null) sb.setAtmosphere(sbDto.getAtmosphere());
        if (sbDto.getDuration() != null) sb.setDuration(sbDto.getDuration());
        if (sbDto.getLocation() != null) sb.setLocation(sbDto.getLocation());
        if (sbDto.getTime() != null) sb.setTime(sbDto.getTime());
        
        // 如果关键信息变了，重新生成提示词
        sb.setImagePrompt(promptOrchestrator.generateImagePrompt(sb));
        sb.setVideoPrompt(promptOrchestrator.generateVideoPrompt(sb));
        
        return storyboardRepository.save(sb);
    }

    @Override
    @Transactional
    public void deleteStoryboard(Long id) {
        storyboardRepository.deleteById(id);
    }

    @Transactional
    protected void saveStoryboards(Long episodeId, List<StoryboardAIResult.StoryboardItem> items) throws Exception {
        // 删除旧分镜（物理删除，因为是重新生成）
        // 注意：JPA 默认没有 deleteByEpisodeId，使用循环删除或自定义查询
        List<Storyboard> oldStoryboards = storyboardRepository.findByEpisodeIdOrderByStoryboardNumberAsc(episodeId);
        storyboardRepository.deleteAll(oldStoryboards);

        for (StoryboardAIResult.StoryboardItem item : items) {
            Storyboard sb = new Storyboard();
            sb.setEpisodeId(episodeId);
            sb.setSceneId(item.getSceneId());
            sb.setStoryboardNumber(item.getShotNumber());
            sb.setTitle(item.getTitle());
            sb.setLocation(item.getLocation());
            sb.setTime(item.getTime());
            sb.setShotType(item.getShotType());
            sb.setAngle(item.getAngle());
            sb.setMovement(item.getMovement());
            sb.setDuration(item.getDuration());
            sb.setDialogue(item.getDialogue());
            sb.setAction(item.getAction());
            sb.setAtmosphere(item.getAtmosphere());
            sb.setCharacters(objectMapper.writeValueAsString(item.getCharacters()));
            
            // 简单的提示词编排占位
            sb.setImagePrompt(item.getLocation() + ", " + item.getAction());
            sb.setVideoPrompt(item.getLocation() + ", " + item.getMovement() + ", " + item.getAction());
            
            sb.setStatus("pending");
            storyboardRepository.save(sb);
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String buildPrompt(String script, String characters, String scenes) {
        // 这里简化了 Prompt，实际可以从 Go 版本的模板复制更完整的内容
        return String.format("请将以下剧本拆解为分镜头 JSON 格式。\n" +
                "【剧本原文】\n%s\n" +
                "【可用角色列表】\n%s\n" +
                "【可用场景列表】\n%s\n" +
                "请严格按照 JSON 格式输出：{\"storyboards\": [...] }", script, characters, scenes);
    }
}
