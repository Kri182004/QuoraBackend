package com.quora.quora_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionDto {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;
    
    @NotBlank(message = "Question body is required")
    private String questionBody;
    
    private String userId;
    private String username;

}