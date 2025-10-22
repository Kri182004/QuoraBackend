package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnswerResponseDto addAnswer(String questionId, AnswerRequestDto answerRequestDto, String username) {
        
        // 1. Find the user who is posting
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the question being answered to ensure it exists
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found with ID: " + questionId));

        // 3. Create the new Answer object (using your model's fields)
        Answer newAnswer = Answer.builder()
                .content(answerRequestDto.getContent())
                .questionId(question.getId()) // Set the String ID
                .userId(currentUser.getId()) // Set the String ID
                .username(currentUser.getUsername()) // Store the username
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .voteCount(0) // Set default vote count
                .build();
        
        // 4. Save the new answer
        Answer savedAnswer = answerRepository.save(newAnswer);

        // 5. Link the new answer to the question's list of answers
        question.getAnswers().add(savedAnswer);
        questionRepository.save(question);

        // 6. Convert the saved Answer to an AnswerResponseDto and return it
        return mapToAnswerResponseDto(savedAnswer);
    }

    // Helper method to convert Model to DTO (using your model's fields)
    private AnswerResponseDto mapToAnswerResponseDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUserId())
                .username(answer.getUsername())
                .questionId(answer.getQuestionId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .voteCount(answer.getVoteCount())
                .upvotes(answer.getUpvotes())
                .build();
    }
}