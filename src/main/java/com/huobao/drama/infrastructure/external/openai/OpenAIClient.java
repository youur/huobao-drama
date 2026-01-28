package com.huobao.drama.infrastructure.external.openai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.util.List;

@FeignClient(name = "openaiClient", url = "http://placeholder") // 添加占位 URL 防止初始化报错
public interface OpenAIClient {

    @PostMapping
    ChatCompletionResponse chatCompletion(
            URI baseUrl,
            @RequestHeader("Authorization") String authorization,
            @RequestBody ChatCompletionRequest request
    );

    @PostMapping
    ImageGenerationResponse generateImage(
            URI baseUrl,
            @RequestHeader("Authorization") String authorization,
            @RequestBody ImageGenerationRequest request
    );

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ChatMessage {
        private String role;
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ChatCompletionRequest {
        private String model;
        private List<ChatMessage> messages;
        private Double temperature;
        private Integer max_tokens;
        private Integer max_completion_tokens;
        private Double top_p;
        private Boolean stream;
    }

    @Data
    class ChatCompletionResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;

        @Data
        public static class Choice {
            private Integer index;
            private ChatMessage message;
            private String finish_reason;
        }

        @Data
        public static class Usage {
            private Integer prompt_tokens;
            private Integer completion_tokens;
            private Integer total_tokens;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ImageGenerationRequest {
        private String model;
        private String prompt;
        private Integer n;
        private String size;
    }

    @Data
    class ImageGenerationResponse {
        private Long created;
        private List<ImageData> data;

        @Data
        public static class ImageData {
            private String url;
            private String b64_json;
        }
    }
}
