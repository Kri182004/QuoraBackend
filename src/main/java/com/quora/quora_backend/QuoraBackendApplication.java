package com.quora.quora_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.quora.quora_backend.repository")
@EnableElasticsearchRepositories(basePackages = "com.quora.quora_backend.repository.elastic")
@SpringBootApplication
public class QuoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoraBackendApplication.class, args);
    }

}