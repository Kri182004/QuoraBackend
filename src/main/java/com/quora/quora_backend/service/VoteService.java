package com.quora.quora_backend.service;

import com.quora.quora_backend.model.Answer; 
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.model.Vote;
import com.quora.quora_backend.model.VoteType;
import com.quora.quora_backend.repository.AnswerRepository; 
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository; 
    private final UserRepository userRepository;

    @Transactional
    public void voteOnQuestion(String questionId, VoteType voteType) {
        // 1. Get the Question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        // 2. Get the current User
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found in database"));

        // 3. Check if the user has already voted on this question
        Optional<Vote> existingVoteOpt = voteRepository.findByUserAndQuestion(currentUser, question);

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();
           if (existingVote.getVoteType() == voteType) {
                // User clicked the same button again (e.g., upvoted an already upvoted post)
                // This is an "un-vote"
                voteRepository.delete(existingVote);
                
                // Revert the vote count
                int countChange = voteType == VoteType.UPVOTE ? -1 : 1;
                question.setVoteCount(question.getVoteCount() + countChange);
                
            } else {
                // User changed their vote (e.g., from upvote to downvote)
                existingVote.setVoteType(voteType);
                voteRepository.save(existingVote);
                
                // Adjust the count (e.g., UPVOTE to DOWNVOTE is -2)
                int countChange = voteType == VoteType.UPVOTE ? 2 : -2;
                question.setVoteCount(question.getVoteCount() + countChange);
            }
        } else {
            // 4. If no existing vote, create a new one
            Vote newVote = Vote.builder()
                    .voteType(voteType)
                    .question(question)
                    .user(currentUser)
                    .build();
            voteRepository.save(newVote);
            int countChange = voteType == VoteType.UPVOTE ? 1 : -1;
            question.setVoteCount(question.getVoteCount() + countChange);
        }

        // 5. Save the updated question
        questionRepository.save(question);
    }

    // --- ADD THIS ENTIRE NEW METHOD ---

    @Transactional
    public void voteOnAnswer(String answerId, VoteType voteType) {
        // 1. Get the Answer
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalStateException("Answer not found"));

        // 2. Get the current User
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found in database"));

        // 3. Check if the user has already voted on this answer
        Optional<Vote> existingVoteOpt = voteRepository.findByUserAndAnswer(currentUser, answer);

        if (existingVoteOpt.isPresent()) {

            Vote existingVote = existingVoteOpt.get();
            if (existingVote.getVoteType() == voteType) {
                // User clicked the same button again (un-vote)
                voteRepository.delete(existingVote);
                
                // Revert the vote count
                int countChange = voteType == VoteType.UPVOTE ? -1 : 1;
                answer.setVoteCount(answer.getVoteCount() + countChange);

            } else {
                // User changed their vote
                existingVote.setVoteType(voteType);
                voteRepository.save(existingVote);
                
                // Adjust the count
                int countChange = voteType == VoteType.UPVOTE ? 2 : -2;
                answer.setVoteCount(answer.getVoteCount() + countChange);
            }
        } else {
            // 4. If no existing vote, create a new one
            Vote newVote = Vote.builder()
                    .voteType(voteType)
                    .answer(answer) 
                    .user(currentUser)
                    .build();
            voteRepository.save(newVote);
            int countChange = voteType == VoteType.UPVOTE ? 1 : -1;
            answer.setVoteCount(answer.getVoteCount() + countChange);
        }

        // 5. Save the updated answer
        answerRepository.save(answer);
    }
}