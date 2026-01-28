package com.huobao.drama.application.service.impl;

import com.huobao.drama.application.service.PromptOrchestrator;
import com.huobao.drama.domain.model.Storyboard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromptOrchestratorImpl implements PromptOrchestrator {

    @Override
    public String generateImagePrompt(Storyboard sb) {
        List<String> parts = new ArrayList<>();

        // 1. 场景背景与时间
        if (sb.getLocation() != null && !sb.getLocation().isEmpty()) {
            String locationDesc = sb.getLocation();
            if (sb.getTime() != null && !sb.getTime().isEmpty()) {
                locationDesc += ", " + sb.getTime();
            }
            parts.add(locationDesc);
        }

        // 2. 角色初始姿态
        if (sb.getAction() != null && !sb.getAction().isEmpty()) {
            String initialPose = extractInitialPose(sb.getAction());
            if (!initialPose.isEmpty()) {
                parts.add(initialPose);
            }
        }

        // 3. 情绪/氛围
        if (sb.getAtmosphere() != null && !sb.getAtmosphere().isEmpty()) {
            parts.add(sb.getAtmosphere());
        }

        // 4. 风格
        parts.add("anime style, high quality, masterpiece");

        return String.join(", ", parts);
    }

    @Override
    public String generateVideoPrompt(Storyboard sb) {
        List<String> parts = new ArrayList<>();

        if (sb.getAction() != null) parts.add("Action: " + sb.getAction());
        if (sb.getMovement() != null) parts.add("Camera movement: " + sb.getMovement());
        if (sb.getShotType() != null) parts.add("Shot type: " + sb.getShotType());
        if (sb.getAngle() != null) parts.add("Angle: " + sb.getAngle());
        if (sb.getLocation() != null) parts.add("Scene: " + sb.getLocation());
        
        parts.add("Style: anime style");

        return String.join(". ", parts);
    }

    private String extractInitialPose(String action) {
        // 模拟 Go 版本的逻辑：在动作词之前截断
        String[] processWords = {"然后", "接着", "接下来", "随后", "紧接着", "向下", "向上", "开始", "逐渐"};
        String result = action;
        for (String word : processWords) {
            int idx = result.indexOf(word);
            if (idx > 0) {
                result = result.substring(0, idx);
                break;
            }
        }
        return result.replaceAll("[，。,. ]+$", "").trim();
    }
}
