package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
import com.quora.quora_backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            Authentication authentication
    ) {
        
        CommentResponseDto newComment;
        String username = authentication.getName();

        // --- THIS IS THE FINAL LOGIC ---
        if (commentRequestDto.getAnswerId() != null) {
            // This is a comment on an answer
            newComment = commentService.addCommentToAnswer(commentRequestDto, username);
        } else if (commentRequestDto.getQuestionId() != null) {
            // This is a comment on a question
            newComment = commentService.addCommentToQuestion(commentRequestDto, username);
        } else if (commentRequestDto.getParentCommentId() != null) {
            // This is a reply to another comment
            newComment = commentService.addReplyToComment(commentRequestDto, username);
        } else {
            // If no ID is provided, it's an invalid request
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        // --- END OF FINAL LOGIC ---

        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @RequestParam(required = false) String answerId,
            @RequestParam(required = false) String questionId
    ) {
        List<CommentResponseDto> comments = commentService.getCommentsForParent(answerId, questionId);
        return ResponseEntity.ok(comments);
    }
}