package com.quora.quora_backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.repository.QuestionRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository=questionRepository;
    }
    public Question saveQuestion(Question question){
        return questionRepository.save(question);
    }
    public Optional<Question> getQuestionById(String id){
        return questionRepository.findById(id);
    }   
    public Question addAnswer(String questionId,Answer answer){
        Optional<Question> optionalQuestion=questionRepository.findById(questionId);
        if(optionalQuestion.isPresent()){
            Question question=optionalQuestion.get();
            question.getAnswers().add(answer);
            return questionRepository.save(question);
        }
        return null;
    }
}
