package com.quora.quora_backend.dto;

import com.quora.quora_backend.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteEvent {
    
    private String voterUsername; // Who did the vote
    private VoteType voteType; // UPVOTE or DOWNVOTE
    
    // What was voted on?
    private String questionId;
    private String answerId;
    
    // Who should be notified?
    private String questionOwnerId;
    private String answerOwnerId;
}
/* This DTO is very specific. It tells our NotificationListener who voted,
 what they voted on (a question or an answer), and who owns that content so we know who to notify.*/