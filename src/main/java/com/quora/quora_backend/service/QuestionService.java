package com.quora.quora_backend.service;

// CORRECT: Import the classes we created
import com.quora.quora_backend.document.QuestionDocument;
import com.quora.quora_backend.repository.AnswerRepository;
import com.quora.quora_backend.repository.QuestionRepository;
import com.quora.quora_backend.repository.UserRepository;
import com.quora.quora_backend.repository.elastic.QuestionElasticRepository;
import com.quora.quora_backend.dto.AnswerResponseDto;
import com.quora.quora_backend.dto.QuestionDetailsDto;
import com.quora.quora_backend.dto.QuestionRequestDto;
import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.model.Answer;
import com.quora.quora_backend.model.Question;
import com.quora.quora_backend.model.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionElasticRepository questionElasticRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository, QuestionElasticRepository questionElasticRepository,UserRepository userRepository,AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.questionElasticRepository = questionElasticRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }

     public QuestionDetailsDto getQuestionWithAnswers(String questionId) {
        // 1. Find the question by its ID
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        // 2. Find all answers for that question using our new repository method
        List<Answer> answers = answerRepository.findByQuestionId(questionId);

        // 3. Convert the list of Answer objects to a list of AnswerResponseDto objects
        List<AnswerResponseDto> answerDtos = answers.stream()
                .map(this::convertAnswerToDto) // We'll create this helper method
                .collect(Collectors.toList());

        // 4. Create the final DTO and combine all the data
        QuestionDetailsDto questionDetailsDto = new QuestionDetailsDto();
        questionDetailsDto.setId(question.getId());
        questionDetailsDto.setTitle(question.getTitle());
        questionDetailsDto.setQuestionBody(question.getQuestionBody());
        questionDetailsDto.setUserId(question.getUserId());
        questionDetailsDto.setUsername(question.getUsername());
        questionDetailsDto.setCreatedAt(question.getCreatedAt());
        questionDetailsDto.setUpdatedAt(question.getUpdatedAt());
        questionDetailsDto.setAnswers(answerDtos); // Set the list of answers

        return questionDetailsDto;
    }

    // --- ADD THIS NEW HELPER METHOD ---
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
        return dto;
    }
    public QuestionResponseDto saveQuestion(QuestionRequestDto questionRequestDto,String username) {//added the username parameter because we need to link the question to the user
        //find the user in the db using the username from the token you got during authentication
        User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found: " + username));
        //create the new Question object and set all the fields including userId and username

        Question question = new Question();
        question.setTitle(questionRequestDto.getTitle());
        question.setQuestionBody(questionRequestDto.getQuestionBody());
        question.setUserId(user.getId());
        question.setUsername(user.getUsername());

        // 1. Save to MongoDB (primary database)
        Question savedQuestion = questionRepository.save(question);

        // 2. ALSO save to Elasticsearch (for searching)
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(savedQuestion.getId());
        questionDocument.setTitle(savedQuestion.getTitle());
        questionDocument.setContent(savedQuestion.getQuestionBody());

        // FIX: You must save the 'questionDocument', not the 'savedQuestion'
        questionElasticRepository.save(questionDocument);

        return convertToResponseDto(savedQuestion);
    }

    public QuestionResponseDto convertToResponseDto(Question question) {
        return new QuestionResponseDto(
                question.getId(),
                question.getTitle(),
                question.getQuestionBody(),
                question.getUserId(),
                question.getUsername(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
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
    // 1. Find all questions from the primary database (MongoDB)
    List<Question> questions = questionRepository.findAll();

    // 2. Convert each Question object into a QuestionResponseDto
    return questions.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
}
}