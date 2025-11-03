package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Structure for the event sent when an answer is created or updated
public class AnswerEvent {
    private String answerId;
    private String questionId;
    private String authorUsername;
    private String questionOwnerId;

}
/*instead of just sending a plain string like "new answer!", we're sending a structured object. 
This gives our "Notification Service" (which we'll build later) all the
 information it needs to send a useful notification. */
 /*Gives the Notification Service everything it needs to send a proper “someone answered your question” message.

Makes data transfer structured, expandable, and reliable. */