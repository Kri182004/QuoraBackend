package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Or Instant, match your User model
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private String id;
    private String username;
    // Add other user details you might want to show, e.g., registration date
    // private LocalDateTime registrationDate;

    // We'll populate these lists
    private List<QuestionResponseDto> questionsAsked;
    private List<AnswerResponseDto> answersGiven;

}
/*This DTO combines information from multiple sources (User, Question, Answer) into a single object, perfect for displaying a user's profile page on the frontend. */