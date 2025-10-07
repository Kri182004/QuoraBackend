package com.quora.quora_backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="question")//this is used to specify the collection name
public class Question {
    @Id
    private String id;
    private String userId;
    private String username;

    @NotBlank(message="Title is required")
    @Size(min=10,max=100,message="Title must be between 2 and 100 characters")//since we dont have afixed schema thats why we are having a loy of validations
    private String title;

    @NotBlank(message = "Question body is required")
    @Size(min = 10, max = 1000, message = "Question body must be between 10 and 1000 characters")
    private String questionBody;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private List<Answer> answers = new ArrayList<>(); 
    //By adding = new ArrayList<>() to the declaration, 
    //you guarantee that the answers list is never null.

}
