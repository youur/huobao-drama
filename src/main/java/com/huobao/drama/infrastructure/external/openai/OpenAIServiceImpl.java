package com.huobao.drama.infrastructure.external.openai;

import com.huobao.drama.application.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("openAIService")
@RequiredArgsConstructor
public class OpenAIServiceImpl implements AIService {

    private final OpenAIClient openAIClient;

    @Override
    public String generateText(String prompt, String systemPrompt, String model, String baseUrl, String apiKey, String endpoint) {
        List<OpenAIClient.ChatMessage> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(OpenAIClient.ChatMessage.builder().role("system").content(systemPrompt).build());
        }
        messages.add(OpenAIClient.ChatMessage.builder().role("user").content(prompt).build());

        OpenAIClient.ChatCompletionRequest request = OpenAIClient.ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .build();

        String auth = "Bearer " + apiKey;
        String fullUrl = baseUrl + (endpoint != null ? endpoint : "/v1/chat/completions");

        try {
            OpenAIClient.ChatCompletionResponse response = openAIClient.chatCompletion(URI.create(fullUrl), auth, request);
            return extractContent(response);
        } catch (Exception e) {
            log.warn("OpenAI request failed, trying with max_completion_tokens fallback", e);
            // Fallback logic for models that prefer max_completion_tokens (like o1)
            if (e.getMessage().contains("max_tokens")) {
                request.setMax_tokens(null);
                request.setMax_completion_tokens(1000); // Default or from config
                OpenAIClient.ChatCompletionResponse response = openAIClient.chatCompletion(URI.create(fullUrl), auth, request);
                return extractContent(response);
            }
            throw e;
        }
    }

    private String extractContent(OpenAIClient.ChatCompletionResponse response) {
        if (response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("No choices in OpenAI response");
        }
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public List<String> generateImage(String prompt, String size, int n, String model, String baseUrl, String apiKey) {
        OpenAIClient.ImageGenerationRequest request = OpenAIClient.ImageGenerationRequest.builder()
                .model(model)
                .prompt(prompt)
                .n(n)
                .size(size)
                .build();

        String auth = "Bearer " + apiKey;
        String fullUrl = baseUrl + "/v1/images/generations";

        OpenAIClient.ImageGenerationResponse response = openAIClient.generateImage(URI.create(fullUrl), auth, request);
        
        return response.getData().stream()
                .map(data -> data.getUrl() != null ? data.getUrl() : "data:image/png;base64," + data.getB64_json())
                .collect(Collectors.toList());
    }
}
