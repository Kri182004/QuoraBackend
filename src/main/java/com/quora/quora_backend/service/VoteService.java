package com.quora.quora_backend.service;

import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.model.Vote;
import com.quora.quora_backend.model.VoteType;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository; // <-- ADD THIS IMPORT
import com.quora.quora_backend.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- ADD THIS IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository; // <-- ADD THIS FIELD

    @Transactional
    public void voteOnQuestion(String questionId, VoteType voteType) {
        // 1. Get the Question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        // 2. Get the current User (THE NEW, ROBUST WAY)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found in database"));

        // 3. Check if the user has already voted on this question
        Optional<Vote> existingVoteOpt = voteRepository.findByUserAndQuestion(currentUser, question);

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();
            if (existingVote.getVoteType() != voteType) {
                // Change vote (e.g., from UPVOTE to DOWNVOTE)
                existingVote.setVoteType(voteType);
                voteRepository.save(existingVote);
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
}