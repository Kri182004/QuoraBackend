package com.quora.quora_backend.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository("questionElasticRepository")
public interface QuestionElasticRepository extends ElasticsearchRepository<QuestionElasticDocument, String> {
    //this repository wull give you all the
    //standard methods for interacting with Elasticsearch index

}
