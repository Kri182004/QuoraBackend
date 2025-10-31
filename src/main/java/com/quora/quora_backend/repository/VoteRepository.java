package com.quora.quora_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.model.Vote;

@Repository
public interface VoteRepository extends MongoRepository<Vote,String> {//we have interface because we do not need to provide implementation for the methods,spring data mongodb will provide the implementation,
//we just need to define the methods we want to use and <Vote,String> means we are working with Vote objects and the ID type is String
Optional<Vote> findByUserAndQuestion(User user, Question question);
//this method will help us to find if a user has already voted on a question,
//so that we can prevent multiple votes from the same user on the same question
Optional<Vote> findByUserAndAnswer(User user, Answer answer);
//this method will help us to find if a user has already voted on an answer,
//so that we can prevent multiple votes from the same user on the same answer
List<Vote> findByAnswer(Answer answer);//this method will help us to get all votes for a particular answer

List<Vote> findByQuestion(Question question); //this method will help us to get all votes for a particular question


}



//⚙️ How will the flow work?
//Later, when we write the voting logic, this repository will be crucial for checking if a user has already voted. The flow will be:

//A user tries to upvote a question.

//Our code will call the voteRepository.findByUserAndQuestion(...) method.

//if the Optional result contains a Vote: We know the user has voted on this question before. We can then decide to either change their existing vote or prevent them from voting again.

//If the Optional result is empty: We know this is a brand new vote, and we can proceed to save it.