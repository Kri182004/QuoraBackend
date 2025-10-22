package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Comment;
import com.quora.quora_backend.model.Question; // <-- NEW IMPORT
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.QuestionRepository; // <-- NEW IMPORT
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository; // <-- ADD THIS FIELD

    // This is your existing method, no changes
    @Transactional
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new IllegalStateException("Answer not found"));
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .answer(answer)
                .build();
        Comment savedComment = commentRepository.save(newComment);
        return mapToCommentResponseDto(savedComment);
    }

    // --- ADD THIS ENTIRE NEW METHOD ---
    @Transactional
    public CommentResponseDto addCommentToQuestion(CommentRequestDto commentRequestDto, String username) {
        
        // 1. Find the logged-in user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the question they are commenting on
        Question question = questionRepository.findById(commentRequestDto.getQuestionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        // 3. Create the new Comment object
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .question(question) // <-- We link it to the question
                .build();

        // 4. Save the comment
        Comment savedComment = commentRepository.save(newComment);

        // 5. Return the new DTO
        return mapToCommentResponseDto(savedComment);
    }
    
    // This is your existing helper method, no changes
    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();
    }
}