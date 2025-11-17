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
import org.springframework.web.bind.annotation.*;

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
        String username = authentication.getName();
        CommentResponseDto newComment;

        if (commentRequestDto.getAnswerId() != null) {
            newComment = commentService.addCommentToAnswer(commentRequestDto, username);

        } else if (commentRequestDto.getQuestionId() != null) {
            newComment = commentService.addCommentToQuestion(commentRequestDto, username);

        } else if (commentRequestDto.getParentCommentId() != null) {
            newComment = commentService.addReplyToComment(commentRequestDto, username);

        } else {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @RequestParam(required = false) String answerId,
            @RequestParam(required = false) String questionId
    ) {
        return ResponseEntity.ok(commentService.getCommentsForParent(answerId, questionId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String commentId,
            Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
