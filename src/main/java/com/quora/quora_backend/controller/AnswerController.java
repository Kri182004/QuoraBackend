package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<AnswerResponseDto> addAnswerToQuestion(
            @PathVariable String questionId,
            @Valid @RequestBody AnswerRequestDto answerRequestDto,
            Authentication authentication
    ) {
       
        AnswerResponseDto newAnswer = answerService.addAnswer(questionId, answerRequestDto, authentication.getName());
        return new ResponseEntity<>(newAnswer, HttpStatus.CREATED);
    
    } 
}