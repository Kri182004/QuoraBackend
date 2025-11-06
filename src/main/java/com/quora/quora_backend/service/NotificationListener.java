package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = KafkaConfig.NOTIFICATIONS_TOPIC,
        groupId = "quora-backend-group",
        containerFactory = "kafkaListenerContainerFactory"
)
public class NotificationListener {

    @KafkaHandler
    public void handleAnswerEvent(@Payload AnswerEvent event) {
        System.out.println("✅ [Kafka Consumer] Received AnswerEvent: " + event);
        // TODO: add notification sending logic here (email/push/db record)
    }

    @KafkaHandler
    public void handleCommentEvent(@Payload CommentEvent event) {
        System.out.println("✅ [Kafka Consumer] Received CommentEvent: " + event);
        // TODO: add notification sending logic here (email/push/db record)
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(@Payload Object event) {
        System.out.println("⚠️ [Kafka Consumer] Unknown event type: " + event);
    }
}
