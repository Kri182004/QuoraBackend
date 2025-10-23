package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> searchQuestions(@RequestParam("q") String query) {
        List<QuestionResponseDto> results = questionService.searchQuestions(query);
        return ResponseEntity.ok(results);
    }
}