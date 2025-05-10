package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// The overall response from OpenAI
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(
        String id,
        String object,
        Long created,
        List<Choice> choices
) {
}
