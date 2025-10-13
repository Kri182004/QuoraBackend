package com.quora.quora_backend.service;

// CORRECT: Import the classes we created
import com.quora.quora_backend.document.QuestionDocument;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.elastic.QuestionElasticRepository;

import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionElasticRepository questionElasticRepository;
    private final UserRepository userRepository;

    public QuestionService(QuestionRepository questionRepository, QuestionElasticRepository questionElasticRepository,UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.questionElasticRepository = questionElasticRepository;
        this.userRepository = userRepository;
    }
    public QuestionResponseDto saveQuestion(QuestionRequestDto questionRequestDto,String username) {//added the username parameter because we need to link the question to the user
        //find the user in the db using the username from the token you got during authentication
        User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found: " + username));
        //create the new Question object and set all the fields including userId and username

        Question question = new Question();
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUserId(user.getId());
        question.setUsername(user.getUsername());

        // 1. Save to MongoDB (primary database)
        Question savedQuestion = questionRepository.save(question);

        // 2. ALSO save to Elasticsearch (for searching)
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(savedQuestion.getId());
        questionDocument.setTitle(savedQuestion.getTitle());
        questionDocument.setContent(savedQuestion.getQuestionBody());

        // FIX: You must save the 'questionDocument', not the 'savedQuestion'
        questionElasticRepository.save(questionDocument);

        return convertToResponseDto(savedQuestion);
    }

    public QuestionResponseDto convertToResponseDto(Question question) {
        return new QuestionResponseDto(
                question.getId(),
                question.getTitle(),
                question.getQuestionBody(),
                question.getUserId(),
                question.getUsername(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    public Optional<Question> getQuestionById(String id) {
        return questionRepository.findById(id);
    }

    public List<QuestionResponseDto> getAllQuestionsByUserId(String userId) {
        List<Question> questions = questionRepository.findByUserId(userId);
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    public List<QuestionResponseDto> searchQuestions(String query) {
        List<QuestionDocument> documents = questionElasticRepository.findByTitleContainingOrContentContaining(query, query);

        List<String> ids = documents.stream().map(QuestionDocument::getId).collect(Collectors.toList());
        
        
        List<Question> questions = (List<Question>) questionRepository.findAllById(ids);
        
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    public List<QuestionResponseDto> getAllQuestions() {
    // 1. Find all questions from the primary database (MongoDB)
    List<Question> questions = questionRepository.findAll();

    // 2. Convert each Question object into a QuestionResponseDto
    return questions.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
}
}