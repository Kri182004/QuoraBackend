package com.quora.quora_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.quora.quora_backend.model.Answer;
@Repository
public interface AnswerRepository extends MongoRepository<Answer,String> {
List<Answer> findByQuestionId(String questionId);
List<Answer> findByUserId(String userId);
}
