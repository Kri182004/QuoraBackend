package com.quora.quora_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.quora.quora_backend.model.Question;

@Repository
public interface QuestionRepository  extends MongoRepository<Question, String> {

}
