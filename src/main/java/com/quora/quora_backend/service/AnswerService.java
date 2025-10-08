package com.quora.quora_backend.service;

import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.repository.AnswerRepository;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository){
        this.answerRepository = answerRepository;
    }

    public Answer addAnswer(String questionId, AnswerRequestDto answerRequestDto) {
        Answer answer = new Answer();
        answer.setContent(answerRequestDto.getContent());
        answer.setUserId(answerRequestDto.getUserId());
        answer.setUsername(answerRequestDto.getUsername());
        answer.setQuestionId(questionId); // Setting the question ID from the URL

        return answerRepository.save(answer);
    }
}