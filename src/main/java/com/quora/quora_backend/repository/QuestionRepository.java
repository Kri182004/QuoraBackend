package com.quora.quora_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.Topic;

@Repository
public interface QuestionRepository  extends MongoRepository<Question, String> {
 List<Question> findByUserId(String userId);//method to find questions by user ID
 List<Question> findByTopicsContains(Topic topic);//method to find questions by topic

}
