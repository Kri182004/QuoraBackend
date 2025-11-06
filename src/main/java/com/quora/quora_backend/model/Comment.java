package com.quora.quora_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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

    @CreatedDate // Automatically sets creation time
    private Instant createdAt;
    
    @LastModifiedDate // Automatically sets update time
    private Instant updatedAt;

    // --- FIX 1: Changed from String IDs to @DBRef objects ---

    // user who made the comment
    @DBRef
    private User user;

    // The comment is on EITHER a question OR an answer
    @DBRef
    private Question question;

    @DBRef
    private Answer answer;

    // --- FIX 2: Fixed parent/child relationship ---

    // This is the parent (if this comment is a reply)
    @DBRef
    @JsonBackReference // Prevents infinite JSON loops
    private Comment parentComment;

    // These are the replies to THIS comment
    @DBRef
    @Builder.Default
    @JsonManagedReference // Prevents infinite JSON loops
    private List<Comment> replies = new ArrayList<>();
}