package com.quora.quora_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data// Lombok annotation to generate getters, setters, toString, etc.
@AllArgsConstructor
@NoArgsConstructor
@Builder// Lombok annotation to implement the builder pattern,gives a flexible way to create objects
@Document(collection = "votes")// Specify the collection name in MongoDB
public class Vote {

    @Id
    private String id;

    private VoteType voteType;//we can have only two types of votes upvote and downvote

    @DBRef//this annotation is used to indicate that a field should be treated as a reference to another document in a different collection,
    //in easy words it is used to create a relationship between two documents in MongoDB
   //When we save a Vote, MongoDB will just store the ID of the user, 
   //the ID of the question, etc. 
   //This is very efficient and avoids duplicating data.
    private User user;

    @DBRef
    private Question question;
//question and answer fiels are optional because a vote can be associated with either a question or an answer, 
//but not both at the same time.
    @DBRef
    private Answer answer;
}












//How will the flow work?
//A user is looking at a question or an answer in the final application.
//->
//They click the "upvote" button.
//->
//The frontend sends a request to our backend API (which we will build later).
//->
//Our backend code will create a new Vote object.
//->
//It will set the voteType to UPVOTE, link the user who voted, and link the question or answer that was voted on.
//->
//Finally, it will save this Vote object into the "votes" collection in our MongoDB database.
//->
//This creates a permanent record that "User A upvoted Question X". The system can now prevent User A from upvoting Question X again.