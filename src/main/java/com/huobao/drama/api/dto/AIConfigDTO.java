package com.huobao.drama.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AIConfigDTO {
    private Long id;
    
    @NotBlank(message = "Service type is required")
    @JsonProperty("service_type")
    private String serviceType;
    
    private String provider;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Base URL is required")
    @JsonProperty("base_url")
    private String baseUrl;
    
    @NotBlank(message = "API key is required")
    @JsonProperty("api_key")
    private String apiKey;
    
    private Object model;
    private String endpoint;
    
    @JsonProperty("query_endpoint")
    private String queryEndpoint;
    
    private Integer priority;
    
    @JsonProperty("is_default")
    private Boolean isDefault;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    private String settings;
}
