package com.example.demo.service;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatCompletionRequest;
import com.example.demo.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ChatGptService {

    private final WebClient openAiClient;

    public ChatGptService(WebClient.Builder builder,
                          @Value("${openai.api.base-url}") String baseUrl,
                          @Value("${openai.api.key}") String apiKey) {
        this.openAiClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    public Mono<ChatCompletionResponse> chat(List<ChatMessage> history) {
        ChatCompletionRequest req = new ChatCompletionRequest(
                "gpt-3.5-turbo",
                history,
                0.7,
                1000
        );
        return openAiClient.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class);
    }
}