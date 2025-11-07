package com.quora.quora_backend.kafka;

import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.model.Notification;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.NotificationRepository;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @KafkaListener(topics = "comment-topic", groupId = "quora-backend-group")
    public void handleCommentEvent(CommentEvent event) {

        User userToNotify = null;
        String message = null;

        // Determine whether the comment was on an Answer or Question
        if ("ANSWER".equals(event.getType())) {
            // Find the Answer
            Answer answer = answerRepository.findById(event.getParentId()).orElse(null);
            if (answer != null) {
                // Fetch the User who owns the Answer
                userToNotify = userRepository.findById(answer.getUserId()).orElse(null);
                message = "New comment on your answer: " + event.getMessage();
            }
        } else if ("QUESTION".equals(event.getType())) {
            // Find the Question
            Question question = questionRepository.findById(event.getParentId()).orElse(null);
            if (question != null) {
                // Fetch the User who owns the Question
                userToNotify = userRepository.findById(question.getUserId()).orElse(null);
                message = "New comment on your question: " + event.getMessage();
            }
        }

        // Avoid notifying the user who made the comment
        if (userToNotify != null && !userToNotify.getId().equals(event.getUserId())) {
            Notification notification = Notification.builder()
                    .senderId(event.getUserId())      // who made the comment
                    .receiverId(userToNotify.getId()) // who should be notified
                    .content(message)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            System.out.println("Notification created for user: " + userToNotify.getUsername());
        }
    }
}
