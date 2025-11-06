package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private String id;
    private String content;
    private String username;
    private String questionId;
    private String answerId;
    private String parentCommentId;
    private Instant createdAt;
}
