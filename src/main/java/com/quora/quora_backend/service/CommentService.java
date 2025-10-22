package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto; // <-- ADD THIS IMPORT
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Comment;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Transactional
    // CHANGE 1: Update the return type here
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {
        
        // 1. Find the logged-in user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the answer they are commenting on
        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        // 3. Create the new Comment object
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .answer(answer) 
                .build();

        // 4. Save the comment
        Comment savedComment = commentRepository.save(newComment);

        // CHANGE 2: Return the new DTO by calling the helper method
        return mapToCommentResponseDto(savedComment);
    }

    // --- ADD THIS HELPER METHOD ---
    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                // Set the correct ID based on which parent is not null
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();
    }
}