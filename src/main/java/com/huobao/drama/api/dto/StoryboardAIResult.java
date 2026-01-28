package com.huobao.drama.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StoryboardAIResult {
    private List<StoryboardItem> storyboards;
    private int total;

    @Data
    public static class StoryboardItem {
        @JsonProperty("shot_number")
        private int shotNumber;
        
        private String title;
        
        @JsonProperty("shot_type")
        private String shotType;
        
        private String angle;
        private String time;
        private String location;
        
        @JsonProperty("scene_id")
        private Long sceneId;
        
        private String movement;
        private String action;
        private String dialogue;
        private String result;
        private String atmosphere;
        private String emotion;
        private int duration;
        
        @JsonProperty("bgm_prompt")
        private String bgmPrompt;
        
        @JsonProperty("sound_effect")
        private String soundEffect;
        
        private List<Long> characters;
        
        @JsonProperty("is_primary")
        private boolean isPrimary;
    }
}
