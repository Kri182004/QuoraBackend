package com.quora.quora_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.quora.quora_backend.repository.elastic") // Specify Elastic repos again here
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        // This configures how Spring connects to your Elasticsearch server.
        // Make sure Elasticsearch is running on localhost:9200.
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }
}