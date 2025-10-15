package com.quora.quora_backend.service;

import java.security.Security;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.model.Vote;
import com.quora.quora_backend.model.VoteType;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.VoteRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
   
    @Transactional
    public void voteOnQuestion(String questionId,VoteType voteType){
        //1.get the question and current user
        Question question=questionRepository.findById(questionId)
        .orElseThrow(()->new IllegalStateException("Question not found with id: "+questionId));
    
        User currentUser=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    //2.check if the user has already voted on this question
    Optional<Vote> existingVoteOpt=voteRepository.findByUserAndQuestion(currentUser, question);
   if(existingVoteOpt.isPresent()){
    Vote existingVote=existingVoteOpt.get();
    //3.if the user has already voted, we need to check if they are
    // changing their vote or trying to vote the same way again
    if(existingVote.getVoteType()!=voteType){
        //updating the existing vote
        existingVote.setVoteType(voteType);
        voteRepository.save(existingVote);
        //update the question's vote count accordingly
       int countChange=voteType==VoteType.UPVOTE?2:-2;
         question.setVoteCount(question.getVoteCount()+countChange);
    }else{
        //4.if no existing vote found, create a new vote
        Vote newVote = Vote.builder()
                    .voteType(voteType)
                    .question(question)
                    .user(currentUser)
                    .build();
        voteRepository.save(newVote);
        //update the question's vote count accordingly
        int countChange=voteType==VoteType.UPVOTE?1:-1;
         question.setVoteCount(question.getVoteCount()+countChange);
    }
    //5.save the updated question
    questionRepository.save(question);
   }
}

}
