package com.quora.quora_backend.service;

import com.quora.quora_backend.config.KafkaConfig;
import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.AnswerRequestDto;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final KafkaTemplate<String, Object> kafkaTemplate; 
    // 👆 changed <String, AnswerEvent> → <String, Object>
    // Reason: we will now send multiple object types (AnswerEvent, CommentEvent, etc.)

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public AnswerResponseDto addAnswer(String questionId, AnswerRequestDto answerRequestDto, String username) {

        // 1️⃣ Find user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2️⃣ Find question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found with ID: " + questionId));

        // 3️⃣ Create answer
        Answer newAnswer = Answer.builder()
                .content(answerRequestDto.getContent())
                .questionId(question.getId())
                .userId(currentUser.getId())
                .username(currentUser.getUsername())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .voteCount(0)
                .build();

        // 4️⃣ Save answer
        Answer savedAnswer = answerRepository.save(newAnswer);
        question.getAnswers().add(savedAnswer);
        questionRepository.save(question);

        // 5️⃣ Send Kafka event
 try {
    AnswerEvent event = AnswerEvent.builder()
            .answerId(savedAnswer.getId())
            .questionId(question.getId())
            .authorUsername(currentUser.getUsername())
            .questionOwnerId(question.getUserId())
            .build();
    
    // This simple send now works, because our KafkaConfig (Bean 4)
    // automatically adds the correct "__TypeId__" header.
    kafkaTemplate.send(KafkaConfig.NOTIFICATIONS_TOPIC, event);
    
    System.out.println("===> KAFKA PRODUCER: Sent AnswerEvent ✅");

} catch (Exception e) {
    System.err.println("Failed to send Kafka message: " + e.getMessage());
}

        // 6️⃣ Return DTO
        return mapToAnswerResponseDto(savedAnswer);
    }

    @Transactional
    public void deleteAnswer(String answerId, String username) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalStateException("Answer not found with ID: " + answerId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!answer.getUserId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not authorized to delete this answer");
        }

        commentRepository.deleteAll(commentRepository.findByAnswerId(answerId));
        voteRepository.deleteAll(voteRepository.findByAnswer(answer));

        Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
        if (question != null) {
            question.getAnswers().remove(answer);
            questionRepository.save(question);
        }
        answerRepository.delete(answer);
    }

    @Transactional
    public AnswerResponseDto updateAnswer(String answerId, AnswerRequestDto requestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        if (!answer.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("You are not authorized to update this answer");
        }

        answer.setContent(requestDto.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        Answer updatedAnswer = answerRepository.save(answer);

        return mapToAnswerResponseDto(updatedAnswer);
    }

    public List<AnswerResponseDto> getAnswersForQuestion(String questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream().map(this::mapToAnswerResponseDto).collect(Collectors.toList());
    }

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