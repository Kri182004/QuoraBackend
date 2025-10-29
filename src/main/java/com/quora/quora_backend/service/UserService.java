package com.quora.quora_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.dto.UserProfileDto;
import com.quora.quora_backend.dto.UserRegistrationDto;
import com.quora.quora_backend.dto.UserResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;    


    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,
    AnswerService answerService,QuestionRepository questionRepository,
    QuestionService questionService,AnswerRepository answerRepository){
        this.answerService=answerService;
        this.userRepository = userRepository;
        this.passwordEncoder =passwordEncoder;
        this.questionService=questionService;
        this.questionRepository=questionRepository;
        this.answerRepository=answerRepository;

    }

    public User registerUser(UserRegistrationDto userDto){
        //check username if already exist
        if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        //encode the password before saving it
        String hashedPassword=passwordEncoder.encode(userDto.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    @Transactional(readOnly = true) // Use readOnly for performance on GET requests
    public UserProfileDto getUserProfile(String userId) {
        // 1. Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        // 2. Find all questions asked by this user
        List<Question> questions = questionRepository.findByUserId(userId);

        // 3. Find all answers given by this user
        List<Answer> answers = answerRepository.findByUserId(userId); // <-- Need to add findByUserId to AnswerRepository

        // 4. Convert questions and answers to DTOs
        // (Using helper methods or injected services is best)
        List<QuestionResponseDto> questionDtos = questions.stream()
                .map(questionService::convertToResponseDto) // Reuse QuestionService's mapper
                .collect(Collectors.toList());

        List<AnswerResponseDto> answerDtos = answers.stream()
                // You'll need a public mapper in AnswerService or copy the logic here
                .map(answerService::mapToAnswerResponseDto) // Assuming mapToAnswerResponseDto is public or accessible
                .collect(Collectors.toList());

        // 5. Build and return the profile DTO
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                // .registrationDate(user.getRegistrationDate()) // Add if you have this field
                .questionsAsked(questionDtos)
                .answersGiven(answerDtos)
                .build();
    }
}