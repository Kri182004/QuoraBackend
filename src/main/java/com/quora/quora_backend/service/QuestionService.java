package com.quora.quora_backend.service;

import com.quora.quora_backend.document.QuestionDocument;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.TopicRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.elastic.QuestionElasticRepository;
import com.quora.quora_backend.dto.*;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.Topic;
import com.quora.quora_backend.model.User;

import org.springframework.data.elasticsearch.ResourceNotFoundException;
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

    public QuestionService(QuestionRepository questionRepository,
                           QuestionElasticRepository questionElasticRepository,
                           UserRepository userRepository,
                           AnswerRepository answerRepository,
                           TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.questionElasticRepository = questionElasticRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.topicRepository = topicRepository;
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

        QuestionResponseDto dto = new QuestionResponseDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setQuestionBody(question.getQuestionBody());
        dto.setUserId(question.getUserId());
        dto.setUsername(question.getUsername());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setTopics(topicDtos);
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
    @Transactional(readOnly = true) // Use readOnly for GET requests
    public List<QuestionResponseDto> getFeed() {
        // 1. Fetch all questions, sorted by newest first
        List<Question> questions = questionRepository.findAllByOrderByCreatedAtDesc();

        // 2. Convert to DTOs
        return questions.stream()
                .map(this::convertToResponseDto) // Reuse your existing converter
                .collect(Collectors.toList());
    }
}