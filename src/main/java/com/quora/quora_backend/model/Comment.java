package com.quora.quora_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    private String id;

    private String content;

    private Instant createdAt;
    private Instant updatedAt;

    // user who made the comment
    private String userId;
    private String username;

    // either questionId or answerId will be set depending on where comment was made
    private String questionId;
    private String answerId;

    // parent comment id if this is a reply
    private String parentCommentId;

    // replies (optional, not always populated)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
}
