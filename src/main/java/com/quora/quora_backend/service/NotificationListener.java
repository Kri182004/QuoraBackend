package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.CommentEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(
        topics = {
                KafkaConfig.ANSWER_TOPIC,
                KafkaConfig.COMMENT_TOPIC,
                KafkaConfig.NOTIFICATIONS_TOPIC
        },
        groupId = "quora-backend-group"
)
public class NotificationListener {

    // 👂 Handle Answer Events
    @KafkaHandler
    public void handleAnswerEvent(@Payload AnswerEvent event) {
        System.out.println("📩 [Kafka Consumer] Received AnswerEvent: " + event);
        // TODO: logic to notify question owner (or log it)
    }

    // 👂 Handle Comment Events
    @KafkaHandler
    public void handleCommentEvent(@Payload CommentEvent event) {
        System.out.println("💬 [Kafka Consumer] Received CommentEvent: " + event);
        // TODO: notify answer author or update analytics
    }

    // 👂 Fallback for unknown events
    @KafkaHandler(isDefault = true)
    public void handleUnknown(@Payload Object event) {
        System.out.println("⚠️ [Kafka Consumer] Unknown event type: " + event);
    }
}
