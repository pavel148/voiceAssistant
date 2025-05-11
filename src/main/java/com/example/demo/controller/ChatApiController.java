package com.example.demo.controller;


import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatMessage;
import com.example.demo.service.ChatGptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatApiController {

    private final ChatGptService chatGptService;

    public ChatApiController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatCompletionResponse> chat(@RequestBody List<ChatMessage> history) {
        ChatCompletionResponse resp = chatGptService.chat(history).block();
        return ResponseEntity.ok(resp);
    }
}