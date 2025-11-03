package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.service.QuestionService; // Need QuestionService to get the feed
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        @RequestParam(value="size",defaultValue="10") int size,
        Authentication authentication  
    ) {
        String username = authentication.getName();
        // We can use the username if we want to customize the feed per user in the future
        Page<QuestionResponseDto> feedPage = questionService.getFeed(page,size,username);
        return ResponseEntity.ok(feedPage);
    }
}

/*Authentication authentication: We're adding this parameter to the method. Spring Security will automatically see this and inject the authenticated user's details from their JWT.

String username = authentication.getName();: This is how we securely get the username of the currently logged-in user.

questionService.getFeed(page, size, username): We now pass this username to our "smart" service method, which will use it to find the user's followed topics. */