package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.*;
import com.quora.quora_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    // ------------------------------------------------------------
    // 1️⃣ COMMENT ON ANSWER
    // ------------------------------------------------------------
    @Transactional
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)); // <-- FIX 1: Removed extra "D"
        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .answer(answer)
                .build();
        Comment savedComment = commentRepository.save(newComment);

        // 🔥 SEND COMMENT EVENT
        try {
            CommentEvent event = CommentEvent.builder()
                    .commentId(savedComment.getId())
                    .commentAuthorUsername(currentUser.getUsername())
                    .answerId(answer.getId())
                    .answerOwnerId(answer.getUserId())  // notify answer author
                    .build();

            System.out.println("===> KAFKA PRODUCER: Sent CommentEvent (for Answer) ✅");
            kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);

        } catch (Exception e) {
            System.err.println("Failed to send comment event to Kafka: " + e.getMessage());
        }

        return mapToCommentResponseDto(savedComment);
    }

    // ------------------------------------------------------------
    // 2️⃣ COMMENT ON QUESTION
    // ------------------------------------------------------------
    @Transactional
    public CommentResponseDto addCommentToQuestion(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Question question = questionRepository.findById(commentRequestDto.getQuestionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .question(question)
                .build();
        Comment savedComment = commentRepository.save(newComment);

        // --- THIS KAFKA BLOCK IS NOW CORRECTED ---
        try {
            CommentEvent event = CommentEvent.builder()
                    .commentId(savedComment.getId())
                    .commentAuthorUsername(currentUser.getUsername())
                    .questionId(question.getId()) // <-- FIX 2: Was using answer.getId()
                    .questionOwnerId(question.getUserId()) // <-- FIX 3: Was using answer.getUserId()
                    .build();

            kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
            System.out.println("===> KAFKA PRODUCER: Sent CommentEvent (for Question) ✅");

        } catch (Exception e) {
            System.err.println("Failed to send Kafka message for new question comment: " + e.getMessage());
        }
        // --- END OF FIX ---

        return mapToCommentResponseDto(savedComment);
    }

    // ------------------------------------------------------------
    // 3️⃣ REPLY TO COMMENT
    // ------------------------------------------------------------
    @Transactional
    public CommentResponseDto addReplyToComment(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Comment parentComment = commentRepository.findById(commentRequestDto.getParentCommentId())
                .orElseThrow(() -> new IllegalStateException("Parent comment not found"));

        Comment newReply = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .parentComment(parentComment)
                .build();
        Comment savedReply = commentRepository.save(newReply);
        
        parentComment.getReplies().add(savedReply);
        commentRepository.save(parentComment);

        // --- THIS KAFKA BLOCK IS NOW CORRECTED ---
        try {
            CommentEvent event = CommentEvent.builder()
                    .commentId(savedReply.getId())
                    .commentAuthorUsername(currentUser.getUsername())
                    .parentCommentId(parentComment.getId())
                    .parentCommentOwnerId(parentComment.getUser().getId())
                    .build();
            
            kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
            System.out.println("===> KAFKA PRODUCER: Sent CommentEvent (for Reply) ✅");

        } catch (Exception e) {
            System.err.println("Failed to send Kafka message for new reply: " + e.getMessage());
        }
        // --- END OF FIX ---

        return mapToCommentResponseDto(savedReply);
    }

    // ------------------------------------------------------------
    // 4️⃣ FETCH COMMENTS
    // ------------------------------------------------------------
    public List<CommentResponseDto> getCommentsForParent(String answerId, String questionId) {
        List<Comment> comments;

        if (answerId != null) {
            comments = commentRepository.findByAnswerId(answerId);
        } else if (questionId != null) {
            comments = commentRepository.findByQuestionId(questionId);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(this::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

    // --- This helper method is correct ---
    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentResponseDto> replyDtos = comment.getReplies().stream()
                    .map(this::mapToCommentResponseDto)
                    .collect(Collectors.toList());
            dto.setReplies(replyDtos);
        }

        return dto;
    }

    // ------------------------------------------------------------
    // 5️⃣ DELETE COMMENT
    // ------------------------------------------------------------
    @Transactional
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("You are not authorized to delete this comment.");
        }

        if (comment.getParentComment() != null) {
            Comment parentComment = comment.getParentComment();
            parentComment.getReplies().remove(comment);
            commentRepository.save(parentComment);
        }

        commentRepository.delete(comment);
    }
}