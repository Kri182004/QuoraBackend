package com.quora.quora_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;

    private String type; // "COMMENT", "UPVOTE", etc.

    private String content; // e.g., comment content or message

    private String senderId; // who performed the action

    private String receiverId; // who should be notified

    private boolean isRead;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID().toString();
        createdAt = Instant.now();
        isRead = false;
    }
}
