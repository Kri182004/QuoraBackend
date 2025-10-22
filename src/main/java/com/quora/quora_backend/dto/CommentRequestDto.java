package com.quora.quora_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {

    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    // These will tell us what the user is commenting on
    private String answerId;
    private String questionId;
    private String parentCommentId;
}