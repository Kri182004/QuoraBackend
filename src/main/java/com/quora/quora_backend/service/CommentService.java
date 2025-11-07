package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.CommentEvent;
import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;

import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Comment;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;


    @Autowired
    private KafkaTemplate<String,Object>kafkaTemplate;

    
    @Transactional
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + commentRequestDto.getAnswerId()));
        
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser) // <-- Uses User object
                .answer(answer)   // <-- Uses Answer object
                .build();
        
        Comment savedComment = commentRepository.save(newComment);
       //kafka eevnt
       CommentEvent commentEvent = new CommentEvent(
            savedComment.getId(),
            answer.getId(),
            "ANSWER",
            currentUser.getId(),
            savedComment.getContent()
    );
       sendCommentEvent(savedComment);

       
        return mapToCommentResponseDto(savedComment);
    }

    
    @Transactional
    public CommentResponseDto addCommentToQuestion(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Question question = questionRepository.findById(commentRequestDto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + commentRequestDto.getQuestionId()));
        
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)     // <-- Uses User object
                .question(question) // <-- Uses Question object
                .build();
        Comment savedComment = commentRepository.save(newComment);
        //send kafka event      
        sendCommentEvent(savedComment);

        return mapToCommentResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto addReplyToComment(CommentRequestDto commentRequestDto, String username) {
        
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Comment parentComment = commentRepository.findById(commentRequestDto.getParentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + commentRequestDto.getParentCommentId()));

        Comment newReply = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)           // <-- Uses User object
                .parentComment(parentComment) // <-- Uses Comment object
                .build();

        Comment savedReply = commentRepository.save(newReply);

        // This works because your model initializes replies = new ArrayList<>()
        parentComment.getReplies().add(savedReply);
        commentRepository.save(parentComment);

        sendCommentEvent(savedReply);

        return mapToCommentResponseDto(savedReply);
    }

    public List<CommentResponseDto> getCommentsForParent(String answerId, String questionId) {
        List<Comment> comments;

        if (answerId != null) {
            // Find only top-level comments (where parentComment is null) for an answer
            comments = commentRepository.findByAnswerIdAndParentCommentIsNull(answerId);
        } else if (questionId != null) {
            // Find only top-level comments (where parentComment is null) for a question
            comments = commentRepository.findByQuestionIdAndParentCommentIsNull(questionId);
        } else {
            return List.of(); 
        }

        return comments.stream()
                .map(this::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // --- This logic now works perfectly ---
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("You are not authorized to delete this comment.");
        }
        
        // Remove from parent's reply list
        if (comment.getParentComment() != null) {
            Comment parentComment = comment.getParentComment();
            if (parentComment.getReplies() != null) {
                parentComment.getReplies().remove(comment);
                commentRepository.save(parentComment);
            }
        }
        
        deleteCommentAndChildren(comment);
    }

    /**
     * Recursively deletes a comment and all its replies.
     */
    private void deleteCommentAndChildren(Comment comment) {
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<Comment> repliesCopy = List.copyOf(comment.getReplies());
            for (Comment reply : repliesCopy) {
                deleteCommentAndChildren(reply);
            }
        }
        commentRepository.delete(comment);
    }
 
    /**
     * Maps a Comment entity to its DTO, including all nested replies.
     */
    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        List<CommentResponseDto> replyDtos = List.of(); 

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replyDtos = comment.getReplies().stream()
                    .map(this::mapToCommentResponseDto) // <-- Recursive call
                    .collect(Collectors.toList());
        }

        // --- This logic now works perfectly ---
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId()) // <-- Gets ID from User object
                .username(comment.getUser().getUsername()) // <-- Gets Username from User object
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replyDtos)
                .build();
    }
    ///kafka event helper
    private void sendCommentEvent(Comment comment) {
        CommentEvent event = new CommentEvent(
                comment.getId(),
                comment.getAnswer() != null ? comment.getAnswer().getId() :
                        comment.getQuestion() != null ? comment.getQuestion().getId() : null,
                comment.getAnswer() != null ? "ANSWER" : "QUESTION",
                comment.getUser().getId(),
                comment.getContent()
        );

        kafkaTemplate.send("comment-topic", event);
        System.out.println("Sent Kafka Event -> " + event);
    }
}