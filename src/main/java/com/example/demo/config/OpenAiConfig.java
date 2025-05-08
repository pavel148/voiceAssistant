package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

public class OpenAiConfig {

    @Value("${openai.api.base-url}")
    private String baseUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public WebClient openAiClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
}
