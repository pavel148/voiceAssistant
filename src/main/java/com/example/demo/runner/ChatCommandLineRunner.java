package com.example.demo.runner;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatMessage;
import com.example.demo.exception.RateLimitException;
import com.example.demo.service.ChatGptService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ChatCommandLineRunner implements CommandLineRunner {

    private final ChatGptService chatGptService;

    public ChatCommandLineRunner(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @Override
    public void run(String... args) {
        List<ChatMessage> history = List.of(
                new ChatMessage("system", "You are a helpful assistant."),
                new ChatMessage("user", "Привет, как дела?")
        );

        Mono<ChatCompletionResponse> responseMono = chatGptService.chat(history)
                .doOnError(RateLimitException.class, e ->
                        System.err.println("↪ Rate limit hit: " + e.getMessage()))
                .doOnError(e ->
                        System.err.println("Error while calling ChatGPT: " + e.getMessage()))
                // При любых ошибках возвращаем пустой ответ
                .onErrorResume(e -> Mono.just(new ChatCompletionResponse(
                        "n/a", "error", System.currentTimeMillis(), List.of()
                )));

        ChatCompletionResponse response = responseMono.block();

        System.out.println("=== ChatGPT Request ===");
        history.forEach(msg ->
                System.out.printf("[%s] %s%n", msg.role(), msg.content())
        );

        System.out.println("\n=== ChatGPT Response ===");
        if (response != null && !response.choices().isEmpty()) {
            System.out.println(response.choices().get(0).message().content());
        } else {
            System.out.println("No response or an error occurred.");
        }
    }
}