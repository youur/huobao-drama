package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.api.dto.GenerateCharactersRequest;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.application.service.AIService;
import com.huobao.drama.application.service.ScriptGenerationService;
import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.AIServiceConfig;
import com.huobao.drama.domain.model.Drama;
import com.huobao.drama.domain.model.DramaCharacter;
import com.huobao.drama.domain.repository.DramaCharacterRepository;
import com.huobao.drama.domain.repository.DramaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptGenerationServiceImpl implements ScriptGenerationService {

    private final DramaRepository dramaRepository;
    private final DramaCharacterRepository characterRepository;
    private final AIService aiService;
    private final AIConfigService aiConfigService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String generateCharacters(GenerateCharactersRequest request) {
        Long dramaId = Long.parseLong(request.getDramaId());
        dramaRepository.findById(dramaId)
                .orElseThrow(() -> new BusinessException(404, "Drama not found"));

        String taskId = taskService.createTask("character_generation", request.getDramaId()).getId();
        
        processCharacterGeneration(taskId, request);
        
        return taskId;
    }

    @Async("taskExecutor")
    public void processCharacterGeneration(String taskId, GenerateCharactersRequest req) {
        try {
            taskService.updateTaskStatus(taskId, "processing", 10, "正在分析剧本提取角色...");

            Long dramaId = Long.parseLong(req.getDramaId());
            Drama drama = dramaRepository.findById(dramaId).get();

            String outlineText = req.getOutline();
            if (outlineText == null || outlineText.isEmpty()) {
                outlineText = String.format("标题: %s\n描述: %s\n类型: %s", 
                    drama.getTitle(), drama.getDescription(), drama.getGenre());
            }

            int count = req.getCount() > 0 ? req.getCount() : 5;
            String prompt = String.format("请从以下剧本大纲中提取主要角色，并以 JSON 数组格式返回（包含 name, role, description, personality, appearance, voice_style 字段）：\n\n%s\n\n提取数量: %d", outlineText, count);

            List<AIServiceConfig> configs = aiConfigService.listConfigs("text");
            AIServiceConfig config = configs.stream()
                    .filter(c -> req.getModel() == null || c.getModel().equals(req.getModel()))
                    .findFirst()
                    .orElse(configs.isEmpty() ? null : configs.get(0));

            if (config == null) throw new RuntimeException("No text AI config found");

            String response = aiService.generateText(prompt, "你是一个专业的选角导演", config.getModel(), 
                    config.getBaseUrl(), config.getApiKey(), config.getEndpoint());

            taskService.updateTaskStatus(taskId, "processing", 60, "角色提取完成，正在入库...");

            String jsonPart = extractJson(response);
            List<Map<String, String>> result = objectMapper.readValue(jsonPart, new TypeReference<List<Map<String, String>>>() {});

            List<DramaCharacter> createdCharacters = new ArrayList<>();
            for (Map<String, String> charMap : result) {
                String name = charMap.get("name");
                List<DramaCharacter> existing = characterRepository.findByDramaId(dramaId);
                if (existing.stream().anyMatch(c -> c.getName().equals(name))) {
                    continue;
                }

                DramaCharacter character = new DramaCharacter();
                character.setDramaId(dramaId);
                character.setName(name);
                character.setRole(charMap.get("role"));
                character.setDescription(charMap.get("description"));
                character.setPersonality(charMap.get("personality"));
                character.setAppearance(charMap.get("appearance"));
                character.setVoiceStyle(charMap.get("voice_style"));
                
                createdCharacters.add(characterRepository.save(character));
            }

            taskService.updateTaskResult(taskId, createdCharacters);
            log.info("Character generation completed for drama: {}", dramaId);

        } catch (Exception e) {
            log.error("Failed to process character generation", e);
            taskService.updateTaskError(taskId, e);
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("[");
        int end = text.lastIndexOf("]");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
