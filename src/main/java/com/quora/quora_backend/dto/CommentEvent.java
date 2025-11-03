package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {

    private String commentId;
    private String commentAuthorUsername;
    
    // We need to know what was commented on
    private String questionId;
    private String answerId;
    private String parentCommentId;
    
    // We need to know who to notify
    private String questionOwnerId;
    private String answerOwnerId;
    private String parentCommentOwnerId;
}