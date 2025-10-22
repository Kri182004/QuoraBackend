package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
import com.quora.quora_backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // <-- MAKE SURE THIS IMPORT IS HERE

@RestController // <-- THIS WAS THE MISSING ANNOTATION
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            Authentication authentication
    ) {
        
        CommentResponseDto newComment = commentService.addCommentToAnswer(commentRequestDto, authentication.getName());
        
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }
}