package com.quora.quora_backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration

public class KafkaTopicConfig {
    public static final String NOTIFICATION_TOPIC = "notifications";
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}


/*In Spring Boot, the config package is used to keep all the classes
 that set up or configure parts of your application (like Kafka, Security, CORS, etc.).
These classes don’t handle business logic — they prepare the system.
A Spring @Config class that defines the Kafka topic (like a shared mailbox) for messages.
Automatically creates the topic if it doesn’t already exist — no manual Kafka CLI commands needed.
NOTIFICATIONS_TOPIC = "notifications" → defines topic name.
@Bean public NewTopic notificationsTopic() → tells Spring to auto-create this topic at startup.
TopicBuilder.name(...).partitions(1).replicas(1).build() → sets topic settings. */


//AnswerEvent.java → defines what message to send.

//KafkaTopicConfig.java → sets up where to send it.