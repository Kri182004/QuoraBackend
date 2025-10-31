package com.quora.quora_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.quora.quora_backend.controller.AnswerController;
import com.quora.quora_backend.dto.TopicDto;
import com.quora.quora_backend.dto.TopicRequestDto;
import com.quora.quora_backend.exception.ResourceAlreadyExistsException;
import com.quora.quora_backend.model.Topic;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.TopicRepository;
import com.quora.quora_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final AnswerController answerController;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
//method 1=get all topics
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

    //method 2=create a new topic
   @Transactional
public TopicDto createTopic(TopicRequestDto topicRequestDto) {
    //1. check if a topic with this name already exists
    topicRepository.findByName(topicRequestDto.getName()).ifPresent(topic -> {
        throw new ResourceAlreadyExistsException("Topic already exists with name " + topicRequestDto.getName());
    });

    //2. create the new topic object
    Topic newTopic = Topic.builder()
            .name(topicRequestDto.getName())
            .description(topicRequestDto.getDescription())
            .build();

    //3. save the topic to the repository
    Topic savedTopic = topicRepository.save(newTopic);

    //4. return as DTO
    return mapToTopicDto(savedTopic);
}

//FOLLOW TOPIC
    @Transactional
    public void followTopic(String topicId,String username){
        //1.find the topic by id
        Topic topic=topicRepository.findById(topicId)
        .orElseThrow(()-> new ResourceNotFoundException("Topic not found"));
        //2.find the user by username
        User user=userRepository.findByUsername(username)
        .orElseThrow(()->new UsernameNotFoundException(username));
        //3.add topic to user's followed topics
        if(!user.getFollowedTopics().contains(topic)){
        user.getFollowedTopics().add(topic);    
        userRepository.save(user);
        }
    }

//UNFOLLOW TOPIC
    @Transactional
    public void unfollowTopic(String topicId,String username){
        //1.find the topic by id
        Topic topic=topicRepository.findById(topicId)
        .orElseThrow(()-> new ResourceNotFoundException("Topic not found"));
        //2.find the user by username
        User user=userRepository.findByUsername(username)
        .orElseThrow(()->new UsernameNotFoundException(username));
        //3.remove topic from user's followed topics
        if(user.getFollowedTopics().contains(topic)){
        user.getFollowedTopics().remove(topic);    
        userRepository.save(user);
        }
    }
}
