package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private String type; // COMMENT, REPLY, etc.
    private String entityId; // commentId, answerId, questionId
    private String entityType; // ANSWER or QUESTION
    private String userId; // who performed the action
    private String message; // text message to display
}
