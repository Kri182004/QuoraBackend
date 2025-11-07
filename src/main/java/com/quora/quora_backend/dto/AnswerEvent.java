package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Automatically generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor      // Creates a no-argument constructor
@AllArgsConstructor     // Creates a constructor with all fields
public class AnswerEvent {
    private String questionId;
    private String answerId;
    private String userId;
    private String message;
}
/*
 * @Data = handles all boilerplate code — getters, setters, toString(), equals(), hashCode().

@NoArgsConstructor = needed for Spring/Kafka to deserialize JSON properly.

@AllArgsConstructor = lets you easily create new objects like
 */