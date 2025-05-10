package com.example.demo.service;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatCompletionRequest;
import com.example.demo.dto.ChatMessage;
import com.example.demo.exception.RateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
                "gpt-3.5-turbo", history, 0.7, 1000
        );

        return openAiClient.post()
                .uri("/chat/completions")
                .bodyValue(req)
                .retrieve()
                // Если 429 — превращаем в наш RateLimitException, берём заголовок Retry-After
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, resp -> {
                    String ra = resp.headers()
                            .asHttpHeaders()
                            .getFirst("Retry-After");
                    int wait = ra != null ? Integer.parseInt(ra) : 1;
                    return Mono.error(new RateLimitException("Rate limit, retry after " + wait + "s"));
                })
                .bodyToMono(ChatCompletionResponse.class)
                // Экспоненциальный back-off: до 3 попыток, с шагом 2^n секунды
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(8))
                        .filter(RateLimitException.class::isInstance)
                );
    }
}