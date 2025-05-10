package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// A single choice in the response
@JsonIgnoreProperties(ignoreUnknown = true)
public record Choice(
        ChatMessage message,
        @JsonProperty("finish_reason") String finishReason,
        int index
) {
}
