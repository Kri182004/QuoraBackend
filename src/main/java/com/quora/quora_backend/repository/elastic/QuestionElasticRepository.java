package com.quora.quora_backend.repository.elastic;

import com.quora.quora_backend.document.QuestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface QuestionElasticRepository extends ElasticsearchRepository<QuestionDocument, String> {

    /**
     * This method allows searching for questions where the query string appears
     * in either the title or the content. Spring Data Elasticsearch automatically
     * implements this method based on its name.
     *
     * @param title the string to search for in the title
     * @param content the string to search for in the content
     * @return a list of matching QuestionDocuments
     */
    List<QuestionDocument> findByTitleContainingOrContentContaining(String title, String content);
}