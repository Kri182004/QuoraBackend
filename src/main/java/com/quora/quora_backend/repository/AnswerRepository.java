package com.quora.quora_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.quora.quora_backend.model.Answer;
@Repository
public interface AnswerRepository extends MongoRepository<Answer,String> {

}
