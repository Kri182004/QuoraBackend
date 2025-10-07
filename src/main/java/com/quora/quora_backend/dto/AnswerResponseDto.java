package com.quora.quora_backend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDto {
    private String id;
    private String content;
    private String questionId;
    private String userId;
    private String username;
    private int upvotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}