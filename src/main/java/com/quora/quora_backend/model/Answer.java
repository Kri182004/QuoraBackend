package com.quora.quora_backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "answers")
public class Answer {
    
    @Id
    private String id;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 1000, message = "Content must be between 10 and 1000 characters")
    private String content;

    @Indexed
    private String questionId;
    
    @Indexed
    private String userId;

    private String username;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private int upvotes = 0;
    private Integer voteCount = 0;
}

//How will the flow work?
//The process will be very simple and efficient:

//When a user upvotes a question, our application's service layer will do two things simultaneously:

//It will create a new Vote object to record who voted (like we planned in Step 1).

//It will find the Question object that was voted on and simply increment its voteCount field by 1 (e.g., from 42 to 43).

//When another user loads that question, our application will just read the voteCount field (which is now 43) and display it. No counting required!


//n our project, instead of counting every single Vote document each time, we add the voteCount directly to the Question and Answer.
 //We repeat the "total count" data to make our app faster.
//this is a common technique in database design called "denormalization."
//It trades a bit of extra storage space and complexity (we have to remember to update voteCount whenever a vote is added or removed) for much faster read performance.
//This is especially useful in applications like Quora, where reading data (viewing questions and   answers) happens way more often than writing data (voting).


//Data Modeling is creating the blueprint for your database. It’s the process of planning out exactly what information you need to store and how all the different pieces of information relate to each other.

//The Java classes we are creating in the model package (User.java, Question.java, Vote.java) 
//are the actual blueprint for our database. We are doing data modeling right now!