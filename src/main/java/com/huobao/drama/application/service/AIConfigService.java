package com.huobao.drama.application.service;

import com.huobao.drama.api.dto.AIConfigDTO;
import com.huobao.drama.domain.model.AIServiceConfig;

import java.util.List;

public interface AIConfigService {
    AIServiceConfig createConfig(AIConfigDTO dto);
    
    AIServiceConfig getConfig(Long id);
    
    List<AIServiceConfig> listConfigs(String serviceType);
    
    AIServiceConfig updateConfig(Long id, AIConfigDTO dto);
    
    void deleteConfig(Long id);
    
    void testConnection(AIConfigDTO dto);
}
