package com.huobao.drama.api.controller;

import com.huobao.drama.api.dto.AIConfigDTO;
import com.huobao.drama.application.service.AIConfigService;
import com.huobao.drama.common.response.ApiResponse;
import com.huobao.drama.domain.model.AIServiceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/settings/ai-configs")
@RequiredArgsConstructor
public class AIConfigController {

    private final AIConfigService aiConfigService;

    @PostMapping
    public ApiResponse<AIServiceConfig> createConfig(@Valid @RequestBody AIConfigDTO dto) {
        return ApiResponse.success(aiConfigService.createConfig(dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<AIServiceConfig> getConfig(@PathVariable Long id) {
        return ApiResponse.success(aiConfigService.getConfig(id));
    }

    @GetMapping
    public ApiResponse<List<AIServiceConfig>> listConfigs(@RequestParam(required = false) String service_type) {
        return ApiResponse.success(aiConfigService.listConfigs(service_type));
    }

    @PutMapping("/{id}")
    public ApiResponse<AIServiceConfig> updateConfig(@PathVariable Long id, @RequestBody AIConfigDTO dto) {
        return ApiResponse.success(aiConfigService.updateConfig(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        aiConfigService.deleteConfig(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/test")
    public ApiResponse<Void> testConnection(@RequestBody AIConfigDTO dto) {
        aiConfigService.testConnection(dto);
        return ApiResponse.success(null);
    }
}
