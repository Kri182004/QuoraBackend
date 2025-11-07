package com.quora.quora_backend.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.AnswerEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerProducer {
    private final KafkaTemplate<String,Object>kafkaTemplate;
    public void sendMessage(AnswerEvent event){
        kafkaTemplate.send("answer-topic",event);
        System.out.println("Sent message to kafka->"+ event);
    }

}
/*
 * @Service
👉 Marks this class as a Spring service, meaning Spring will automatically manage it (you don’t need to new it manually).

KafkaTemplate<String, AnswerEvent>
👉 This is like a helper that lets your app send messages to Kafka easily.

The first type (String) is the message key.

The second type (AnswerEvent) is the message body (value) — your DTO.

@RequiredArgsConstructor
👉 From Lombok. It automatically creates a constructor for any final fields (so Spring can inject the KafkaTemplate without you writing extra code).

sendMessage() method
👉 This is what you’ll call whenever an event happens (like someone posts an answer).

"answer-topic" is the Kafka topic name where your message will go.

event is the actual AnswerEvent object containing details of that action.

System.out.println()
👉 Just logs that a message was successfully sent (useful when testing).
 */