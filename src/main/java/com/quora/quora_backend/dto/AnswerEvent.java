package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerEvent {
    private String answerId;
    private String questionId;
    private String authorUsername;   // who wrote the answer
    private String questionOwnerId;  // who should be notified
}