package com.quora.quora_backend.service;

import com.quora.quora_backend.document.QuestionDocument;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.CommentRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.TopicRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.VoteRepository;
import com.quora.quora_backend.repository.elastic.QuestionElasticRepository;

import jakarta.annotation.Resource;

import com.quora.quora_backend.dto.*;
import com.quora.quora_backend.exception.UnauthorizedOperationException;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.Topic;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.model.VoteType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionElasticRepository questionElasticRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
private final VoteRepository voteRepository;
    public QuestionService(QuestionRepository questionRepository,
                           QuestionElasticRepository questionElasticRepository,
                           UserRepository userRepository,
                           AnswerRepository answerRepository,
                           TopicRepository topicRepository,
                           CommentRepository commentRepository,
                           VoteRepository voteRepository
                           ) {
        this.questionRepository = questionRepository;
        this.questionElasticRepository = questionElasticRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    private List<Topic> getOrCreateTopics(List<String> topicNames) {
        if (topicNames == null || topicNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Topic> topics = new ArrayList<>();
        for (String name : topicNames) {
            Topic topic = topicRepository.findByName(name)
                    .orElseGet(() -> {
                        Topic newTopic = Topic.builder().name(name).build();
                        return topicRepository.save(newTopic);
                    });
            topics.add(topic);
        }
        return topics;
    }

    @Transactional
    public QuestionResponseDto saveQuestion(QuestionRequestDto questionRequestDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Topic> topics = getOrCreateTopics(questionRequestDto.getTopicNames());

        Question question = new Question();
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUserId(user.getId());
        question.setUsername(user.getUsername());
        question.setTopics(topics);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        Question savedQuestion = questionRepository.save(question);

        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(savedQuestion.getId());
        questionDocument.setTitle(savedQuestion.getTitle());
        questionDocument.setContent(savedQuestion.getQuestionBody());
        
        questionElasticRepository.save(questionDocument);

        return convertToResponseDto(savedQuestion);
    }

    public QuestionResponseDto convertToResponseDto(Question question) {
        List<TopicDto> topicDtos = new ArrayList<>();
        if (question.getTopics() != null) {
            topicDtos = question.getTopics().stream()
                    .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                    .collect(Collectors.toList());
        }

        // Calculate vote counts
        int upvotes = (int) voteRepository.countByQuestionAndVoteType(question, VoteType.UPVOTE);
        int downvotes = (int) voteRepository.countByQuestionAndVoteType(question, VoteType.DOWNVOTE);
        
        // Calculate total comment count (comments on question + comments on all answers)
        int questionComments = (int) commentRepository.countByQuestionId(question.getId());
        
        // Count comments on all answers for this question
        List<Answer> answers = answerRepository.findByQuestionId(question.getId());
        int answerComments = 0;
        for (Answer answer : answers) {
            answerComments += commentRepository.findByAnswerId(answer.getId()).size();
        }
        
        int commentCount = questionComments + answerComments;

        QuestionResponseDto dto = new QuestionResponseDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setQuestionBody(question.getQuestionBody());
        dto.setUserId(question.getUserId());
        dto.setUsername(question.getUsername());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setTopics(topicDtos);
        dto.setUpvotes(upvotes);
        dto.setDownvotes(downvotes);
        dto.setCommentCount(commentCount);
        return dto;
    }

    public QuestionDetailsDto getQuestionWithAnswers(String questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        List<AnswerResponseDto> answerDtos = answers.stream()
                .map(this::convertAnswerToDto)
                .collect(Collectors.toList());

        List<TopicDto> topicDtos = new ArrayList<>();
        if (question.getTopics() != null) {
            topicDtos = question.getTopics().stream()
                    .map(topic -> new TopicDto(topic.getId(), topic.getName()))
                    .collect(Collectors.toList());
        }

        QuestionDetailsDto questionDetailsDto = new QuestionDetailsDto();
        questionDetailsDto.setId(question.getId());
        questionDetailsDto.setTitle(question.getTitle());
        questionDetailsDto.setQuestionBody(question.getQuestionBody());
        questionDetailsDto.setUserId(question.getUserId());
        questionDetailsDto.setUsername(question.getUsername());
        questionDetailsDto.setCreatedAt(question.getCreatedAt());
        questionDetailsDto.setUpdatedAt(question.getUpdatedAt());
        questionDetailsDto.setAnswers(answerDtos);
        questionDetailsDto.setTopics(topicDtos); // This line is now fixed

        return questionDetailsDto;
    }
    
    private AnswerResponseDto convertAnswerToDto(Answer answer) {
        AnswerResponseDto dto = new AnswerResponseDto();
        dto.setId(answer.getId());
        dto.setContent(answer.getContent());
        dto.setQuestionId(answer.getQuestionId());
        dto.setUserId(answer.getUserId());
        dto.setUsername(answer.getUsername());
        dto.setUpvotes(answer.getUpvotes());
        dto.setCreatedAt(answer.getCreatedAt());
        dto.setUpdatedAt(answer.getUpdatedAt());
        dto.setVoteCount(answer.getVoteCount());
        return dto;
    }

    public Optional<Question> getQuestionById(String id) {
        return questionRepository.findById(id);
    }

    public List<QuestionResponseDto> getAllQuestionsByUserId(String userId) {
        List<Question> questions = questionRepository.findByUserId(userId);
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<QuestionResponseDto> searchQuestions(String query) {
        List<QuestionDocument> documents = questionElasticRepository.findByTitleContainingOrContentContaining(query, query);
        List<String> ids = documents.stream().map(QuestionDocument::getId).collect(Collectors.toList());
        List<Question> questions = (List<Question>) questionRepository.findAllById(ids);
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDto> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    //we will now add a method to get questions by topic
    public List<QuestionResponseDto>getQuestionsByTopicName(String topicName){
        //1.find topic by name
        Topic topic=topicRepository.findByName(topicName)        
        .orElseThrow(()->new ResourceNotFoundException("Topic not found with name"+topicName));
        //2.use the question repository to find questions by topic
        List<Question>questions=questionRepository.findByTopicsContains(topic);
        //3.convert the list of questions to list of QuestionResponseDto
        return questions.stream()
        .map(this::convertToResponseDto)
        .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> getFeed(int page, int size, String username) {
        
        // 2. Create the Pageable object (sort by newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Question> questionPage;

        // 3. Check if user is authenticated and has followed topics
        if (username != null) {
            // 1. Find the current user and their followed topics
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            
            List<Topic> followedTopics = currentUser.getFollowedTopics();
            
            if (followedTopics != null && !followedTopics.isEmpty()) {
                // If YES: Fetch a paged list of questions that are in their followed topics
                questionPage = questionRepository.findByTopicsIn(followedTopics, pageable);
            } else {
                // If user has no followed topics: show all recent questions
                questionPage = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } else {
            // If anonymous user: show all recent questions
            questionPage = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        // 4. Convert and return the DTO Page
        return questionPage.map(this::convertToResponseDto);
    }


    @Transactional
    public void deleteQuestion(String questionId,String username){
        //1.find the user
        User currentUser=userRepository.findByUsername(username)
        .orElseThrow(()->new ResourceNotFoundException("User not found with username: "+username));
        //2.find the question to be deleted
        Question question=questionRepository.findById(questionId)
        .orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+questionId));
        //3.CRITICAL OWNERSHIP CHECK
        if(!question.getUserId().equals(currentUser.getId())){
            throw new UnauthorizedOperationException("You are not authorized to delete this question");
        }
        //4.Cascade delete: delete all answers, comments ,vote associated with the question
        List<Answer>answers=answerRepository.findByQuestionId(questionId);
        if(answers!=null && !answers.isEmpty()){
            for(Answer answer:answers){
                commentRepository.deleteAll(commentRepository.findByAnswerId(answer.getId()));
                voteRepository.deleteAll(voteRepository.findByAnswer(answer));
            }
            //delete all answers
answerRepository.deleteAll(answers);
        }
        //delete the comment on the question itself
        commentRepository.deleteAll(commentRepository.findByQuestionId(questionId));
       //delte all votes on question
       voteRepository.deleteAll(voteRepository.findByQuestion(question));
       //5.delte the question from mongo and elasticsearch
        questionRepository.delete(question);
        questionElasticRepository.deleteById(questionId);
    }


    //new method for updating a question
    @Transactional
    public QuestionResponseDto updateQuestion(String questionId,QuestionRequestDto questionRequestDto,String username){
        //1.find the user
        User currentUser=userRepository.findByUsername(username)
        .orElseThrow(()->new ResourceNotFoundException("User not found with username: "+username));
        //2.find the question to be updated
        Question question=questionRepository.findById(questionId)
        .orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+questionId));
        //3.CRITICAL OWNERSHIP CHECK
        if(!question.getUserId().equals(currentUser.getId())){
            throw new UnauthorizedOperationException("You are not authorized to update this question");
        }
        //4.update the question fields
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUpdatedAt(LocalDateTime.now());
        //update topics
        List<Topic>topics=getOrCreateTopics(questionRequestDto.getTopicNames());
        question.setTopics(topics);
        //5.save the updated question
        Question updatedQuestion=questionRepository.save(question);
        //6.update elasticsearch document
        QuestionDocument questionDocument=questionElasticRepository.findById(questionId)
        .orElseThrow(()->new ResourceNotFoundException("Question document not found with id: "+questionId));
        questionDocument.setTitle(updatedQuestion.getTitle());
        questionDocument.setId(updatedQuestion.getId());
        questionDocument.setContent(updatedQuestion.getQuestionBody());
        questionElasticRepository.save(questionDocument);
        //7.return the updated question as DTO
        return convertToResponseDto(updatedQuestion);
    }
}