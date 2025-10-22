package com.quora.quora_backend.repository;

import com.quora.quora_backend.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- ADD THIS IMPORT

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    // --- ADD (OR FIX) THESE TWO METHODS ---

    List<Comment> findByAnswerId(String answerId);
    
    List<Comment> findByQuestionId(String questionId);
}