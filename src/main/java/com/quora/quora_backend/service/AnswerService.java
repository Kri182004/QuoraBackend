package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.User; // <-- NEW IMPORT
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.UserRepository; // <-- NEW IMPORT
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository; // <-- ADD THIS

    // UPDATE THE CONSTRUCTOR
    public AnswerService(AnswerRepository answerRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    // --- THIS ENTIRE METHOD IS REWRITTEN ---
    public Answer addAnswer(String questionId, AnswerRequestDto answerRequestDto, String username) {
        // 1. Find the user in the database using the username from the token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 2. Create the new Answer object
        Answer answer = new Answer();
        answer.setContent(answerRequestDto.getContent());
        answer.setQuestionId(questionId);

        // 3. Set the user details securely from the user object we found
        answer.setUserId(user.getId());
        answer.setUsername(user.getUsername());
        // You can set defaults like upvotes here if you want (e.g., answer.setUpvotes(0);)

        // 4. Save and return the new answer
        return answerRepository.save(answer);
    }
}