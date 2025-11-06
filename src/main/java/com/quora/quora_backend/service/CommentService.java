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

    
    @Transactional
    public CommentResponseDto addCommentToAnswer(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Answer answer = answerRepository.findById(commentRequestDto.getAnswerId())
                .orElseThrow(() -> new IllegalStateException("Answer not found"));
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .answer(answer)
                .build();
        Comment savedComment = commentRepository.save(newComment);
        return mapToCommentResponseDto(savedComment);
    }

    
    @Transactional
    public CommentResponseDto addCommentToQuestion(CommentRequestDto commentRequestDto, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Question question = questionRepository.findById(commentRequestDto.getQuestionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));
        Comment newComment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .question(question)
                .build();
        Comment savedComment = commentRepository.save(newComment);
        return mapToCommentResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto addReplyToComment(CommentRequestDto commentRequestDto, String username) {
        
        // 1. Find the logged-in user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Find the parent comment they are replying to
        Comment parentComment = commentRepository.findById(commentRequestDto.getParentCommentId())
                .orElseThrow(() -> new IllegalStateException("Parent comment not found"));

        // 3. Create the new reply Comment object
        Comment newReply = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(currentUser)
                .parentComment(parentComment) // <-- We link it to its parent
                .build();

        // 4. Save the new reply
        Comment savedReply = commentRepository.save(newReply);

        // 5. Add the new reply to the parent's list of replies and re-save the parent
        parentComment.getReplies().add(savedReply);
        commentRepository.save(parentComment);

        // 6. Return the new reply as a DTO
        return mapToCommentResponseDto(savedReply);
    }

    public List<CommentResponseDto> getCommentsForParent(String answerId, String questionId) {
        List<Comment> comments;

        // 1. Find the top-level comments for either the answer or question
        if (answerId != null) {
            comments = commentRepository.findByAnswerId(answerId);
        } else if (questionId != null) {
            comments = commentRepository.findByQuestionId(questionId);
        } else {
            // If no ID is provided, return an empty list
            return List.of(); 
        }

        // 2. Convert the list of Comment models into a list of DTOs
        return comments.stream()
                .map(this::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }
    // --- This is your existing helper method, no changes ---
   private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        // 1. Map the main (parent) comment
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();

        // 2. Check if this comment has replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            // 3. If it does, map each reply by calling this SAME method
            List<CommentResponseDto> replyDtos = comment.getReplies().stream()
                    .map(this::mapToCommentResponseDto) // <-- RECURSION HAPPENS HERE
                    .collect(Collectors.toList());
            dto.setReplies(replyDtos);
        }

        // 4. Return the complete DTO (now with replies, if any)
        return dto;
    }
    @Transactional
    public void deleteComment(String commentId,String username){
        //1.find the comment to be deleted
        Comment comment=commentRepository.findById(commentId)
        .orElseThrow(()->new ResourceNotFoundException("Comment not found with id: "+commentId));
        //2.Find the ser who is making the request
        User currentUser=userRepository.findByUsername(username)
        .orElseThrow(()->new UsernameNotFoundException("User not found: "+username));
        //OWNERSHIP CHECK
        //3.check if the user's id matches the id of the user who made the comment?
        if(!comment.getUser().getId().equals(currentUser.getId())){
                throw new UnauthorizedOperationException("You are not authorized to delete this comment.");
        }
        //4.if it is a nested comment,we might need to remove it from its parent's reply list
        if(comment.getParentComment()!=null){
                Comment parentComment=comment.getParentComment();
                parentComment.getReplies().remove(comment);
                commentRepository.save(parentComment);
        }
        //5.delete the comment
        commentRepository.delete(comment);
}
}