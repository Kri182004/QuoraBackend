package com.quora.quora_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.service.QuestionService;
import com.quora.quora_backend.service.AnswerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    public QuestionController(QuestionService questionService, AnswerService answerService){
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@Valid @RequestBody QuestionRequestDto questionRequestDto) {
        QuestionResponseDto responseDto = questionService.saveQuestion(questionRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable String id) {
        Optional<Question> question = questionService.getQuestionById(id);
        return question.map(value -> new ResponseEntity<>(questionService.convertToResponseDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestionsByUserId(@PathVariable String userId) {
        List<QuestionResponseDto> questions = questionService.getAllQuestionsByUserId(userId);
        if (!questions.isEmpty()) {
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/search")
public ResponseEntity<List<QuestionResponseDto>> searchQuestions(@RequestParam("query") String query) {
    List<QuestionResponseDto> questions = questionService.searchQuestions(query);
    if (!questions.isEmpty()) {
        return new ResponseEntity<>(questions, HttpStatus.OK);
    } else {
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}

    
    @PostMapping("/{questionId}/answers")
public ResponseEntity<Answer> addAnswerToQuestion(
    @PathVariable String questionId,
    @Valid @RequestBody AnswerRequestDto answerRequestDto
) {
    answerRequestDto.setQuestionId(questionId); // <-- Add this line
    Answer newAnswer = answerService.addAnswer(questionId, answerRequestDto);
    return new ResponseEntity<>(newAnswer, HttpStatus.CREATED);
}
}