package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = KafkaConfig.NOTIFICATIONS_TOPIC,
        groupId = "quora-backend-group", // This must match application.properties
        containerFactory = "kafkaListenerContainerFactory" // This is the magic line
)
public class NotificationListener {

    private final UserRepository userRepository;

    @KafkaHandler
    public void handleAnswerEvent(@Payload AnswerEvent event) {
        // ... (your logic to print "SENDING NOTIFICATION (NEW ANSWER)") ...
        System.out.println("? [Kafka Consumer] Received AnswerEvent: " + event);
    }

    @KafkaHandler
    public void handleCommentEvent(@Payload CommentEvent event) {
        // ... (your logic to print "SENDING NOTIFICATION (NEW COMMENT)") ...
        System.out.println("? [Kafka Consumer] Received CommentEvent: " + event);
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(@Payload Object event) {
        System.out.println("⚠️ [Kafka Consumer] Unknown event type: " + event);
    }
}