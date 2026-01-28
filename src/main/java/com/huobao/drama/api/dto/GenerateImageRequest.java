package com.huobao.drama.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class GenerateImageRequest {
    private Long storyboardId;
    private Long dramaId;
    private Long sceneId;
    private Long characterId;
    private Long propId;
    private String imageType; // character, scene, storyboard
    private String frameType; // first, key, last, panel, action
    private String prompt;
    private String negativePrompt;
    private String provider;
    private String model;
    private String size;
    private String quality;
    private String style;
    private Integer steps;
    private Double cfgScale;
    private Long seed;
    private Integer width;
    private Integer height;
    private List<String> referenceImages;
}
