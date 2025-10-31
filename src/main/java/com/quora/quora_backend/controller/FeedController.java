package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.service.QuestionService; // Need QuestionService to get the feed
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<Page<QuestionResponseDto>> getFeed(
        @RequestParam(value="page",defaultValue="0") int page,
        @RequestParam(value="size",defaultValue="10") int size  
    ) {
        Page<QuestionResponseDto> feedPage = questionService.getFeed(page,size);
        return ResponseEntity.ok(feedPage);
    }
}