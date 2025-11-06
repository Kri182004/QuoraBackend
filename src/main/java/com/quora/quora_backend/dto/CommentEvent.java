package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private String eventType;
    private String commentId;
    private String commentAuthorUsername;
    private String content;
    private String questionId;
    private String answerId;
    private String parentCommentId;
}
