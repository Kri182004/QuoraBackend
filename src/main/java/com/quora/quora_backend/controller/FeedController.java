package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.service.QuestionService; // Need QuestionService to get the feed
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getFeed() {
        List<QuestionResponseDto> feedQuestions = questionService.getFeed();
        return ResponseEntity.ok(feedQuestions);
    }
}