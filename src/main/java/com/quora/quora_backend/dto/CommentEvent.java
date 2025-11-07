package com.quora.quora_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private String commentId;
    private String parentId;
    private String type; // "ANSWER" or "QUESTION"
    private String userId;
    private String message;
}
