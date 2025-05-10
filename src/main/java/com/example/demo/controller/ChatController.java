package com.example.demo.controller;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatMessage;
import com.example.demo.service.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatGptService chatGptService;

    public ChatController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping
    public Mono<ChatCompletionResponse> sendMessage(@RequestBody List<ChatMessage> history) {
        return chatGptService.chat(history);
    }
}