package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Represents a single message in the chat

public record ChatMessage(String role, String content) { }

