package com.quora.quora_backend.repository;

import com.quora.quora_backend.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByAnswerId(String answerId);
    List<Comment> findByQuestionId(String questionId);
}
