package com.quora.quora_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.TopicDto;
import com.quora.quora_backend.model.Topic;
import com.quora.quora_backend.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    public List<TopicDto>getAllTopics(){
        //1.get all topics from the repository
        List<Topic>topics=topicRepository.findAll();
//2.Convert the list of topics to list of TopicDto
        return topics.stream()
        .map(this::mapToTopicDto)
        .collect(Collectors.toList());
    }
    ///helper method to map Topic model  to TopicDto
    private TopicDto mapToTopicDto(Topic topic){
        return TopicDto.builder()
        .id(topic.getId())
        .name(topic.getName())
        .build();
    }
    /**Streams & Mapping: We use the standard stream().map().collect() 
     * pattern to convert the List<Topic> (from the database) into the List<TopicDto>
     *  (which is safe to send to the user).
     *  We created a simple mapToTopicDto helper for this. */
}
