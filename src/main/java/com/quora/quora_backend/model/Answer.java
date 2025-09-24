package com.quora.quora_backend.model;

import org.springframework.data.annotation.Id;

import lombok.Data;
@Data
public class Answer {
    @Id
    private String id;
    private String answerBody;
    private String userId;
    private String username;
    private int upvotes=0;
}
