package com.quora.quora_backend.service;

// CORRECT: Import the classes we created
import com.quora.quora_backend.document.QuestionDocument;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.elastic.QuestionElasticRepository;

import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionElasticRepository questionElasticRepository;

    public QuestionService(QuestionRepository questionRepository, QuestionElasticRepository questionElasticRepository) {
        this.questionRepository = questionRepository;
        this.questionElasticRepository = questionElasticRepository;
    }

    public QuestionResponseDto saveQuestion(QuestionRequestDto questionRequestDto) {
        Question question = new Question();
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUserId(questionRequestDto.getUserId());
        question.setUsername(questionRequestDto.getUsername());

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

    // FIX: DELETED the old duplicate searchQuestions method. This is the only one.
    public List<QuestionResponseDto> searchQuestions(String query) {
        List<QuestionDocument> documents = questionElasticRepository.findByTitleContainingOrContentContaining(query, query);

        List<String> ids = documents.stream().map(QuestionDocument::getId).collect(Collectors.toList());
        
        // FIX: Corrected the typo from 'question' to 'Question'
        List<Question> questions = (List<Question>) questionRepository.findAllById(ids);
        
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}