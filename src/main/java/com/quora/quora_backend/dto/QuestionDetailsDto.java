package com.quora.quora_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDetailsDto {

    private String id;
    private String title;
    private String questionBody;
    private String userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AnswerResponseDto> answers; // This will hold all the answers

}