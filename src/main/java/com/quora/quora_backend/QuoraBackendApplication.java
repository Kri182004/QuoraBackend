package com.quora.quora_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories; // <-- DELETE THIS LINE
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.quora.quora_backend.repository") // <-- Keep Mongo config
// @EnableElasticsearchRepositories(basePackages = "com.quora.quora_backend.repository.elastic") // <-- DELETE THIS LINE
public class QuoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoraBackendApplication.class, args);
    }

}