package com.quora.quora_backend.service;

import com.quora.quora_backend.dto.CommentRequestDto;
import com.quora.quora_backend.dto.CommentResponseDto;
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

import org.springframework.data.elasticsearch.ResourceNotFoundException;
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

    // COMMENT ON ANSWER
    @Transactional
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + commentRequestDto.getAnswerId()));

        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .answer(answer)
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return mapToCommentResponseDto(savedComment);
    }

    // COMMENT ON QUESTION
    @Transactional
    public CommentResponseDto addCommentToQuestion(CommentRequestDto commentRequestDto, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Question question = questionRepository.findById(commentRequestDto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + commentRequestDto.getQuestionId()));

        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .question(question)
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return mapToCommentResponseDto(savedComment);
    }

    // REPLY TO COMMENT
    @Transactional
    public CommentResponseDto addReplyToComment(CommentRequestDto commentRequestDto, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Comment parentComment = commentRepository.findById(commentRequestDto.getParentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + commentRequestDto.getParentCommentId()));

        Comment newReply = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .parentComment(parentComment)
                .build();

        Comment savedReply = commentRepository.save(newReply);

        parentComment.getReplies().add(savedReply);
        commentRepository.save(parentComment);

        return mapToCommentResponseDto(savedReply);
    }

    // GET COMMENTS FOR A PARENT (ANSWER OR QUESTION)
    public List<CommentResponseDto> getCommentsForParent(String answerId, String questionId) {

        List<Comment> comments;

        if (answerId != null) {
            comments = commentRepository.findByAnswerIdAndParentCommentIsNull(answerId);
        } else if (questionId != null) {
            comments = commentRepository.findByQuestionIdAndParentCommentIsNull(questionId);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(this::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

    // DELETE COMMENT
    @Transactional
    public void deleteComment(String commentId, String username) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("You are not authorized to delete this comment.");
        }

        if (comment.getParentComment() != null) {
            Comment parent = comment.getParentComment();
            parent.getReplies().remove(comment);
            commentRepository.save(parent);
        }

        deleteCommentAndChildren(comment);
    }

    private void deleteCommentAndChildren(Comment comment) {

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<Comment> copy = List.copyOf(comment.getReplies());
            for (Comment reply : copy) {
                deleteCommentAndChildren(reply);
            }
        }
        commentRepository.delete(comment);
    }

    // MAP ENTITY → DTO (recursive)
    private CommentResponseDto mapToCommentResponseDto(Comment comment) {

        List<CommentResponseDto> replyDtos = List.of();

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            replyDtos = comment.getReplies().stream()
                    .map(this::mapToCommentResponseDto)
                    .collect(Collectors.toList());
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replyDtos)
                .build();
    }
}
