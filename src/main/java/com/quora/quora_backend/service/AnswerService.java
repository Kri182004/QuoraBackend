package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public AnswerResponseDto createAnswer(AnswerRequestDto dto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Answer newAnswer = Answer.builder()
                .content(dto.getContent())
                .questionId(question.getId())
                .userId(currentUser.getId())
                .username(currentUser.getUsername())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .voteCount(0)
                .build();

        Answer saved = answerRepository.save(newAnswer);
        // attach answer to question if your model needs, otherwise ensure referential integrity
        // question.getAnswers().add(saved); questionRepository.save(question);

        // Build and send Kafka event
        AnswerEvent event = AnswerEvent.builder()
                .answerId(saved.getId())
                .questionId(question.getId())
                .authorUsername(currentUser.getUsername())
                .content(saved.getContent())
                .build();

        // Send object; JsonSerializer will convert to JSON
        kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);

        return mapToAnswerResponseDto(saved);
    }

    // Map to DTO (as you had)
    public AnswerResponseDto mapToAnswerResponseDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUserId())
                .username(answer.getUsername())
                .questionId(answer.getQuestionId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .voteCount(answer.getVoteCount())
                .upvotes(answer.getUpvotes())
                .build();
    }
}
