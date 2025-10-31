package com.quora.quora_backend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.Topic;

@Repository
public interface QuestionRepository  extends MongoRepository<Question, String> {
 List<Question> findByUserId(String userId);//method to find questions by user ID
 List<Question> findByTopicsContains(Topic topic);//method to find questions by topic
 Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);//// Finds all questions, sorts them by createdAt descending
}
/*
Pageable = tells Spring how to fetch data
Page<Question> = gives you data + page info together

/
 * Pageable

A built-in Spring object that holds pagination info — like page number, page size, and sorting.

You don’t need to create it manually — Spring fills it automatically from request parameters (like ?page=0&size=10).

Used to tell the database which part of the data to fetch.
 */
/*
 * Page<Question>

Instead of just returning a list, Page gives extra info too.

It includes:

The list of questions for the current page

Total number of questions

Total number of pages

Super helpful for the frontend to show “Next Page”, “Total Results”, etc.
 */