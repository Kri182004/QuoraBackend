package com.quora.quora_backend.controller;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.dto.VoteRequestDto;
import com.quora.quora_backend.service.AnswerService;
import com.quora.quora_backend.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final VoteService voteService;

    // Endpoint to create a new answer
    @PostMapping
    public ResponseEntity<AnswerResponseDto> addAnswerToQuestion(
            @RequestParam String questionId,
            @Valid @RequestBody AnswerRequestDto answerRequestDto,
            Authentication authentication
    ) {
        AnswerResponseDto newAnswer = answerService.addAnswer(questionId, answerRequestDto, authentication.getName());
        return new ResponseEntity<>(newAnswer, HttpStatus.CREATED);
    }

    // Endpoint to vote on an answer
    @PostMapping("/{answerId}/vote")
    public ResponseEntity<Void> voteOnAnswer(
            @PathVariable String answerId,
            @RequestBody VoteRequestDto voteRequestDto
    ) {
        voteService.voteOnAnswer(answerId, voteRequestDto.getVoteType());
        return ResponseEntity.ok().build();
    }
    
    // Endpoint to get all answers for a question
    @GetMapping
    public ResponseEntity<List<AnswerResponseDto>> getAnswersForQuestion(
            @RequestParam String questionId
    ) {
        List<AnswerResponseDto> answers = answerService.getAnswersForQuestion(questionId);
        return ResponseEntity.ok(answers);
    }
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void>deleteAnswer(
        @PathVariable String answerId,
        Authentication authentication
    ){
        //get the username of the logged-in user
        String username=authentication.getName();
        //call the service layer to delete the answer
        answerService.deleteAnswer(answerId,username);
        //return no content status
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{answerId}")
public ResponseEntity<AnswerResponseDto>updateAnswer(
    @PathVariable String answerId,
    @Valid @RequestBody AnswerRequestDto answerRequestDto,
    Authentication authentication)  {
        //Get the logged-in user's username
        String username=authentication.getName();
        //Call the service layer to update the answer
        AnswerResponseDto updatedAnswer=answerService.updateAnswer(answerId,answerRequestDto,username);
        //Return the updated answer
        return ResponseEntity.ok(updatedAnswer);

    } 

}