package com.huobao.drama.application.service.impl;

import com.huobao.drama.api.dto.AIConfigDTO;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.application.service.AIService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.AIServiceConfig;
import com.huobao.drama.domain.repository.AIServiceConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIConfigServiceImpl implements AIConfigService {

    private final AIServiceConfigRepository configRepository;
    
    @Qualifier("openAIService")
    private final AIService openAIService;

    @Override
    @Transactional
    public AIServiceConfig createConfig(AIConfigDTO dto) {
        AIServiceConfig config = new AIServiceConfig();
        mapDtoToEntity(dto, config);
        return configRepository.save(config);
    }

    @Override
    public AIServiceConfig getConfig(Long id) {
        return configRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "AI Config not found"));
    }

    @Override
    public List<AIServiceConfig> listConfigs(String serviceType) {
        if (serviceType != null && !serviceType.isEmpty()) {
            return configRepository.findByServiceTypeAndIsActive(serviceType, 1);
        }
        return configRepository.findAll();
    }

    @Override
    @Transactional
    public AIServiceConfig updateConfig(Long id, AIConfigDTO dto) {
        AIServiceConfig config = getConfig(id);
        mapDtoToEntity(dto, config);
        return configRepository.save(config);
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        AIServiceConfig config = getConfig(id);
        config.setDeletedAt(java.time.LocalDateTime.now());
        configRepository.save(config);
    }

    @Override
    public void testConnection(AIConfigDTO dto) {
        try {
            String modelName = "";
            if (dto.getModel() != null) {
                if (dto.getModel() instanceof java.util.List) {
                    java.util.List<?> list = (java.util.List<?>) dto.getModel();
                    if (!list.isEmpty()) modelName = list.get(0).toString();
                } else {
                    modelName = dto.getModel().toString();
                }
            }
            // Test with a simple prompt
            openAIService.generateText("hi", null, modelName, dto.getBaseUrl(), dto.getApiKey(), dto.getEndpoint());
        } catch (Exception e) {
            log.error("AI connection test failed", e);
            throw new BusinessException(400, "Connection test failed: " + e.getMessage());
        }
    }

    private void mapDtoToEntity(AIConfigDTO dto, AIServiceConfig config) {
        if (dto.getServiceType() != null) config.setServiceType(dto.getServiceType());
        if (dto.getProvider() != null) config.setProvider(dto.getProvider());
        if (dto.getName() != null) config.setName(dto.getName());
        if (dto.getBaseUrl() != null) config.setBaseUrl(dto.getBaseUrl());
        if (dto.getApiKey() != null) config.setApiKey(dto.getApiKey());
        
        // 处理 model 字段：可能是 String 或 List
        if (dto.getModel() != null) {
            if (dto.getModel() instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) dto.getModel();
                if (!list.isEmpty()) {
                    config.setModel(list.get(0).toString()); // 取第一个模型名
                }
            } else {
                config.setModel(dto.getModel().toString());
            }
        }
        
        if (dto.getEndpoint() != null) config.setEndpoint(dto.getEndpoint());
        if (dto.getQueryEndpoint() != null) config.setQueryEndpoint(dto.getQueryEndpoint());
        if (dto.getPriority() != null) config.setPriority(dto.getPriority());
        if (dto.getIsDefault() != null) config.setIsDefault(dto.getIsDefault());
        if (dto.getIsActive() != null) config.setIsActive(dto.getIsActive());
        if (dto.getSettings() != null) config.setSettings(dto.getSettings());
    }
}
