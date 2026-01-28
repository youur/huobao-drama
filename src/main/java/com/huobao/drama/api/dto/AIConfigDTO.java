package com.huobao.drama.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AIConfigDTO {
    private Long id;
    
    @NotBlank(message = "Service type is required")
    private String serviceType;
    
    private String provider;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Base URL is required")
    private String baseUrl;
    
    @NotBlank(message = "API key is required")
    private String apiKey;
    
    private String model;
    private String endpoint;
    private String queryEndpoint;
    private Integer priority;
    private Integer isDefault;
    private Integer isActive;
    private String settings;
}
