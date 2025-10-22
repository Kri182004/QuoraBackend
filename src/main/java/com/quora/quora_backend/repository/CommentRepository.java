package com.quora.quora_backend.repository;

import com.quora.quora_backend.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    // We can add custom finder methods here later, like
    // List<Comment> findByAnswer(Answer answer);
    // List<Comment> findByQuestion(Question question);
}