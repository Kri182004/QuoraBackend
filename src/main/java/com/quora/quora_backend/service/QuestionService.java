package com.quora.quora_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.repository.QuestionRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    public QuestionResponseDto saveQuestion(QuestionRequestDto questionRequestDto){
        Question question = new Question();
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUserId(questionRequestDto.getUserId());
        question.setUsername(questionRequestDto.getUsername());
        
        Question savedQuestion = questionRepository.save(question);
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

    public Optional<Question> getQuestionById(String id){
        return questionRepository.findById(id);
    } 

    public List<Question> getAllQuestionsByUserId(String userId) {
        return questionRepository.findByUserId(userId);
    }

    public Question addAnswer(String questionId, AnswerRequestDto answerRequestDto) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            
            Answer answer = new Answer();
            answer.setContent(answerRequestDto.getContent());
            answer.setUserId(answerRequestDto.getUserId());
            answer.setUsername(answerRequestDto.getUsername());

            question.getAnswers().add(answer);
            return questionRepository.save(question);
        }
        return null;
    }
}