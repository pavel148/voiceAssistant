package com.example.demo.controller;

import com.example.demo.dto.ChatCompletionResponse;
import com.example.demo.dto.ChatMessage;
import com.example.demo.service.ChatGptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatWebController {

    private final ChatGptService chatGptService;

    public ChatWebController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("userMessage", "");
        model.addAttribute("botReply", "");
        return "index";
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String userMessage, Model model) {
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system", "You are a helpful assistant."));
        history.add(new ChatMessage("user", userMessage));

        Mono<ChatCompletionResponse> mono = chatGptService.chat(history)
                .onErrorResume(e -> Mono.empty());
        ChatCompletionResponse response = mono.block();
        String reply = (response != null && response.choices() != null && !response.choices().isEmpty())
                ? response.choices().get(0).message().content()
                : "[Ошибка при получении ответа]";

        model.addAttribute("userMessage", userMessage);
        model.addAttribute("botReply", reply);
        return "index";
    }
}