package com.quora.quora_backend.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.AnswerEvent;

@Service
public class AnswerConsumer {
    @KafkaListener(topics="answer-topic",groupId="quora-backend-group")
    public void consume(AnswerEvent event){
System.out.println("📩 Received Kafka Event -> Question ID: " + event.getQuestionId()
                + ", Answer ID: " + event.getAnswerId()
                + ", User ID: " + event.getUserId()
                + ", Message: " + event.getMessage());        
    }

}
