package com.quora.quora_backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean//says"create this bean automatically when the application starts"
    //define a topic for notifications
    public NewTopic notificationTopics(){
        return TopicBuilder
                .name("notificationTopic")//name of the topic,you'll send and read messages from this name
                .partitions(3)//spliting a queue into 3 parts for better performance
                .replicas(1)//number of copies of the topics for fault tolerance
                .build();//build the topic object for us
    }
    public NewTopic commentTopic(){
        return TopicBuilder
               .name("comment-topic")
               .partitions(1)
               .replicas(1)
               .build();
    }

}
//It’s like making a “chat room” where messages can be sent and received.
