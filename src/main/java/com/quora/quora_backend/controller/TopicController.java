package com.quora.quora_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quora.quora_backend.dto.QuestionResponseDto;
import com.quora.quora_backend.dto.TopicDto;
import com.quora.quora_backend.service.QuestionService;
import com.quora.quora_backend.service.TopicService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final QuestionService questionService;
    private final TopicService topicService;
    
    @GetMapping("/{topicName}/questions")
    public ResponseEntity<List<QuestionResponseDto>>getQuestionsByTopic(
        @PathVariable String topicName){
            List<QuestionResponseDto>questions=questionService.getQuestionsByTopicName(topicName);
            return ResponseEntity.ok(questions);
        }

        /**How will the flow work? ⚙️
A user clicks on the "Java" topic tag on the frontend.

The frontend sends a GET request to http://.../api/topics/Java/questions.

Spring routes this request to our new TopicController's getQuestionsByTopic method.

The controller extracts "Java" into the topicName variable.

It calls questionService.getQuestionsByTopicName("Java").

The service finds the "Java" topic, then finds all questions linked to it, converts them to DTOs, and returns the list.

The controller sends this list back to the user. */
@GetMapping
public ResponseEntity<List<TopicDto>>getAllTopics(){
    List<TopicDto>topics=topicService.getAllTopics();
    return ResponseEntity.ok(topics);
}
/**private final TopicService... 
 * → We add this so the controller can use TopicService to get all topics.

@GetMapping → 
Used for “read” requests. It means this method runs when someone visits /api/topics.

ResponseEntity.ok(topics) → 
Sends the list of topics back with a 200 OK message (means success). */
}
