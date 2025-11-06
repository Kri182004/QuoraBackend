package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaTopiConfig;
import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
import com.quora.quora_backend.model.Comment;
import com.quora.quora_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ---------- CREATE ----------

    public CommentResponseDto addCommentToAnswer(CommentRequestDto dto, String username) {
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .answerId(dto.getAnswerId())
                .username(username)
                .userId(dto.getUserId())
                .createdAt(Instant.now())
                .build();

        Comment saved = commentRepository.save(comment);
        publishEvent(saved, "CommentAdded");
        return mapToResponse(saved);
    }

    public CommentResponseDto addCommentToQuestion(CommentRequestDto dto, String username) {
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .questionId(dto.getQuestionId())
                .username(username)
                .userId(dto.getUserId())
                .createdAt(Instant.now())
                .build();

        Comment saved = commentRepository.save(comment);
        publishEvent(saved, "CommentAdded");
        return mapToResponse(saved);
    }

    public CommentResponseDto addReplyToComment(CommentRequestDto dto, String username) {
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .parentCommentId(dto.getParentCommentId())
                .username(username)
                .userId(dto.getUserId())
                .createdAt(Instant.now())
                .build();

        Comment saved = commentRepository.save(comment);
        publishEvent(saved, "ReplyAdded");
        return mapToResponse(saved);
    }

    // ---------- READ ----------

    public List<CommentResponseDto> getCommentsForParent(String answerId, String questionId) {
        if (answerId != null) {
            return commentRepository.findByAnswerId(answerId)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (questionId != null) {
            return commentRepository.findByQuestionId(questionId)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // ---------- DELETE ----------

    public void deleteComment(String commentId, String username) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }

        Comment comment = optional.get();
        if (!comment.getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
        publishEvent(comment, "CommentDeleted");
    }

    // ---------- PRIVATE UTIL METHODS ----------

    private void publishEvent(Comment comment, String eventType) {
        CommentEvent event = CommentEvent.builder()
                .eventType(eventType)
                .commentId(comment.getId())
                .commentAuthorUsername(comment.getUsername())
                .content(comment.getContent())
                .questionId(comment.getQuestionId())
                .answerId(comment.getAnswerId())
                .parentCommentId(comment.getParentCommentId())
                .build();

        kafkaTemplate.send(KafkaTopicConfig.NOTIFICATIONS_TOPIC, event);
    }

    private CommentResponseDto mapToResponse(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(comment.getUsername())
                .questionId(comment.getQuestionId())
                .answerId(comment.getAnswerId())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
