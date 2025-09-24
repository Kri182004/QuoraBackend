package com.quora.quora_backend.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="question")
public class Question {
    @Id
    private String id;
    private String title;
    private String questionBody;
    private String userId;
    private String username;
    private List<Answer>answers=new ArrayList<>();//this line tells our question to conatin a list of answers objects.when we save a question to the data base , all of its answers will be saved along with it allin one in "documents"
}
