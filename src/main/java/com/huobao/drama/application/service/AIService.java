package com.huobao.drama.application.service;

import java.util.List;

public interface AIService {
    String generateText(String prompt, String systemPrompt, String model, String baseUrl, String apiKey, String endpoint);
    
    List<String> generateImage(String prompt, String size, int n, String model, String baseUrl, String apiKey);
}
