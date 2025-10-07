package com.quora.quora_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerDto {

    @NotBlank(message = "Answer body is required")
    private String answerBody;
    
    private String userId;
    private String username;

}