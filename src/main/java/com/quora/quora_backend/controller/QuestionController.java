package com.quora.quora_backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.QuestionDetailsDto;
import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.dto.VoteRequestDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.service.QuestionService;
import com.quora.quora_backend.service.VoteService;
import com.quora.quora_backend.service.AnswerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VoteService voteService;

    public QuestionController(QuestionService questionService, AnswerService answerService,VoteService voteService){
        this.questionService = questionService;
        this.answerService = answerService;
        this.voteService=voteService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@Valid @RequestBody QuestionRequestDto questionRequestDto,Authentication authentication) {
        //get the username from the authentication object of the logged in user
        String username=authentication.getName();
        //pass the username and Dto to service layer
        QuestionResponseDto responseDto = questionService.saveQuestion(questionRequestDto,username);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
public ResponseEntity<QuestionDetailsDto> getQuestionById(@PathVariable String id) {
    try {
        QuestionDetailsDto questionDetails = questionService.getQuestionWithAnswers(id);
        return new ResponseEntity<>(questionDetails, HttpStatus.OK);
    } catch (RuntimeException e) {
        // This catches the "Question not found" exception from the service
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
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
    @Valid @RequestBody AnswerRequestDto answerRequestDto,
    Authentication authentication
) {
// Get the username of the currently logged-in user
    String username = authentication.getName();
    Answer newAnswer = answerService.addAnswer(questionId, answerRequestDto,username);
    return new ResponseEntity<>(newAnswer, HttpStatus.CREATED);
}
@GetMapping //a menthodd for getting all questions
public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
    List<QuestionResponseDto> questions = questionService.getAllQuestions();
    return new ResponseEntity<>(questions, HttpStatus.OK);
}
@PostMapping("/{questionId}/vote")//endpoint to handle voting on a question
    public ResponseEntity<Void> voteOnQuestion(@PathVariable String questionId, @RequestBody VoteRequestDto voteRequestDto) {
        voteService.voteOnQuestion(questionId, voteRequestDto.getVoteType());
        return ResponseEntity.ok().build();
    }
}