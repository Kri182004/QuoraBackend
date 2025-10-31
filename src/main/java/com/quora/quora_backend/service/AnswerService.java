package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public AnswerResponseDto addAnswer(String questionId, AnswerRequestDto answerRequestDto, String username) {
        
        // 1. Find the user who is posting
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the question being answered to ensure it exists
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found with ID: " + questionId));

        // 3. Create the new Answer object (using your model's fields)
        Answer newAnswer = Answer.builder()
                .content(answerRequestDto.getContent())
                .questionId(question.getId()) // Set the String ID
                .userId(currentUser.getId()) // Set the String ID
                .username(currentUser.getUsername()) // Store the username
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .voteCount(0) // Set default vote count
                .build();
        
        // 4. Save the new answer
        Answer savedAnswer = answerRepository.save(newAnswer);

        // 5. Link the new answer to the question's list of answers
        question.getAnswers().add(savedAnswer);
        questionRepository.save(question);

        // 6. Convert the saved Answer to an AnswerResponseDto and return it
        return mapToAnswerResponseDto(savedAnswer);
    }


    @Transactional
    public void deleteAnswer(String answerId, String username) {
        // 1. Find the answer to be deleted
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalStateException("Answer not found with ID: " + answerId));

        // 2. Find the user attempting the deletion
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 3. Check ownership
        if (!answer.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not authorized to delete this answer");
        }
        //4.cascade delete(cleanup related data) if any (e.g., comments,votes)
       //deleta all comments related to this answer
        commentRepository.deleteAll(commentRepository.findByAnswerId(answerId));
        // delete all votes related to this answer
        voteRepository.deleteAll(voteRepository.findByAnswer(answer));
        // 4. Delete the answer
        //remove answer from the question's list of answers
        Question question=questionRepository.findById(answer.getQuestionId()).orElse(null);
        if(question!=null){
            question.getAnswers().remove(answer);
            questionRepository.save(question);
        }
        answerRepository.delete(answer);
    }
    @Transactional
    public AnswerResponseDto updateAnswer(String answerId, AnswerRequestDto requestDto, String username) {
        // 1. Find the user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the answer
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));
         //3.check ownership
         if(!answer.getUserId().equals(currentUser.getId())){
          throw new UnauthorizedOperationException("You are not authorized to update this answer");
        }
        //4.update the answer content and timestamp
        answer.setContent(requestDto.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        //5.save the updated answer
        Answer updatedAnswer=answerRepository.save(answer);
        //6.convert to DTO and return
        return mapToAnswerResponseDto(updatedAnswer);   
}

         


    public List<AnswerResponseDto> getAnswersForQuestion(String questionId) {
        
        // 1. Use the repository to get all raw Answer models
        List<Answer> answers = answerRepository.findByQuestionId(questionId);

        // 2. Convert the list of Answer models into a list of AnswerResponseDto
        return answers.stream()
                .map(this::mapToAnswerResponseDto)
                .collect(Collectors.toList());
    }
    // Helper method to convert Model to DTO (using your model's fields)
    public AnswerResponseDto mapToAnswerResponseDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUserId())
                .username(answer.getUsername())
                .questionId(answer.getQuestionId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .voteCount(answer.getVoteCount())
                .upvotes(answer.getUpvotes())
                .build();
    }
}