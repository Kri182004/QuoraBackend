package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaTopicConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(
            topics = KafkaTopicConfig.NOTIFICATION_TOPIC,
            groupId = "notification-group" // Groups consumers together
    )
    public void handleNotification(AnswerEvent event) {
        // This is where the magic happens!
        // This method automatically runs when a message arrives.
        
        System.out.println("---------------------------------");
        System.out.println("KAFKA MESSAGE RECEIVED:");
        System.out.println("New answer posted!");
        System.out.println("Answer Author: " + event.getAuthorUsername());
        System.out.println("Notify User: " + event.getQuestionOwnerId());
        System.out.println("---------------------------------");

        // LATER: Instead of printing, you would add logic here
        // to send an email, a push notification, or a WebSocket message.
    }
}
/*
 * How will the flow work? ⚙️
You will post an answer to a question (in Insomnia).

Your AnswerService will send the AnswerEvent message to Kafka.

Kafka will receive the message in the notifications topic.

Your NotificationService (which is always listening) will instantly see the message.

It will automatically run the handleNotification method.

You will see the "KAFKA MESSAGE RECEIVED" output printed in your Spring Boot terminal.
 */