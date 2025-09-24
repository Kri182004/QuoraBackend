package com.quora.quora_backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.service.QuestionService;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService){
        this.questionService=questionService;
        
    }
    @PostMapping
    public ResponseEntity<Question>createQuestion(@RequestBody Question question){
        Question newQuestion=questionService.saveQuestion(question);
        return new ResponseEntity<>(newQuestion,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String id){
        Optional<Question> question=questionService.getQuestionById(id);
        return question.map(value->new ResponseEntity<>(value,HttpStatus.OK))
        .orElseGet(()->new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Question> addAnswertoQuestion(@PathVariable String questionId,@RequestBody Answer answer){
    Question updatedQuestion=questionService.addAnswer(questionId,answer);
    if(updatedQuestion!=null){
        return new ResponseEntity<>(updatedQuestion,HttpStatus.OK);
    }else{
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
}
