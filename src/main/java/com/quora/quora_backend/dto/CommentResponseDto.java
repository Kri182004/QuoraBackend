package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private String id;
    private String content;
    private Instant createdAt;
    
    // User info
    private String userId;
    private String username;
    
    // Pointers
    private String questionId;
    private String answerId;
    private String parentCommentId;
}