package com.quora.quora_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopicRequestDto {

    @NotBlank(message = "Topic name cannot be empty")
    private String name;

    private String description;
}
/**This defines the simple structure (name and optional description) 
 * that the user will send in the request body. */