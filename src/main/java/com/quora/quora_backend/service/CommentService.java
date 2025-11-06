package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.model.Comment;
import com.quora.quora_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 1. Add a comment
    @Transactional
    public Comment addComment(Comment comment) {
        comment.setCreatedAt(Instant.now());
        Comment saved = commentRepository.save(comment);

        CommentEvent event = CommentEvent.builder()
                .eventType("CommentAdded")
                .commentId(saved.getId())
                .commentAuthorUsername(saved.getUsername())
                .content(saved.getContent())
                .questionId(saved.getQuestionId())
                .answerId(saved.getAnswerId())
                .parentCommentId(saved.getParentCommentId())
                .build();

        kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
        return saved;
    }

    // 2. Update comment text
    @Transactional
    public Comment updateComment(String commentId, String newText) {
        Optional<Comment> opt = commentRepository.findById(commentId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        Comment c = opt.get();
        c.setContent(newText);
        c.setUpdatedAt(Instant.now());
        Comment updated = commentRepository.save(c);

        CommentEvent event = CommentEvent.builder()
                .eventType("CommentUpdated")
                .commentId(updated.getId())
                .commentAuthorUsername(updated.getUsername())
                .content(updated.getContent())
                .questionId(updated.getQuestionId())
                .answerId(updated.getAnswerId())
                .parentCommentId(updated.getParentCommentId())
                .build();

        kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
        return updated;
    }

    // 3. Delete comment
    @Transactional
    public void deleteComment(String commentId) {
        Optional<Comment> opt = commentRepository.findById(commentId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        Comment c = opt.get();
        commentRepository.deleteById(commentId);

        CommentEvent event = CommentEvent.builder()
                .eventType("CommentDeleted")
                .commentId(c.getId())
                .commentAuthorUsername(c.getUsername())
                .content(c.getContent())
                .questionId(c.getQuestionId())
                .answerId(c.getAnswerId())
                .parentCommentId(c.getParentCommentId())
                .build();

        kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
    }

    // 4. Fetch comments for an answer
    public List<Comment> getCommentsByAnswerId(String answerId) {
        return commentRepository.findByAnswerId(answerId);
    }

    // 5. Fetch comments for a question
    public List<Comment> getCommentsByQuestionId(String questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    // 6. Fetch single comment
    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }
}
