package com.example.demo.runner;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatMessage;
import com.example.demo.service.ChatGptService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

        try {
            ChatCompletionResponse response = chatGptService.chat(history)
                    .doOnError(e -> System.err.println("Error while calling ChatGPT: " + e.getMessage()))
                    .block();

            System.out.println("=== ChatGPT Request ===");
            history.forEach(msg ->
                    System.out.printf("[%s] %s%n", msg.role(), msg.content())
            );

            System.out.println("\n=== ChatGPT Response ===");
            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                String reply = response.choices().get(0).message().content();
                System.out.println(reply);
            } else {
                System.err.println("No response received or empty choices.");
            }
        } catch (Exception ex) {
            System.err.println("Fatal error: " + ex.getMessage());
        }
    }
}
