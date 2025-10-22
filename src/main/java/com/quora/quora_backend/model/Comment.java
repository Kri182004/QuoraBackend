package com.quora.quora_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    @CreatedDate
    @Builder.Default
    private Instant createdAt = Instant.now();

    @DBRef
    private User user;

    // --- Pointers to what this comment belongs to ---
    // A comment will only have ONE of these three fields set.

    @DBRef
    private Question question; // Set if this is a comment on a Question

    @DBRef
    private Answer answer; // Set if this is a comment on an Answer

    @DBRef
    private Comment parentComment; // Set if this is a reply to another Comment

    // --- For nested replies ---
    @DBRef
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
}