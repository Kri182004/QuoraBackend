package com.quora.quora_backend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.quora.quora_backend.model.Question;

public interface QuestionSearchRepository extends ElasticsearchRepository<Question, String>{

}
