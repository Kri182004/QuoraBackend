package com.quora.quora_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "topics")
public class Topic {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Topic name cannot be empty")
    private String name;

    private String description;
    
    // We will add relationships here later, like:
    // @DBRef
    // private List<Question> questions;
    @DBRef(lazy = true) // Use lazy loading for performance  //(line 36-39 added 
    //this tells --->we add a list of questions,dbref indicates the mongo to store info
    //refrences(IDs) lazy=true is a performance optimization -it tells mongo to not load alll the 
    //Questions objects automatically everytime we load a Topic object)
    @Builder.Default // Initialize with an empty list,this ensures that list is always initialized as an empty list when a new topic is created
    
    private List<Question> questions = new ArrayList<>();
}