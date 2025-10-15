package com.quora.quora_backend.model;
import lombok.Data;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data//instead of us writing code for getter,setter,constuctor and toString, @Data will do it (its from lombok)  
@Document(collection = "users")//by spring data mongodb, it will create a collection called users
public class User {
    @Id//this marks the id as primary key
    private String id;
    private String username;
    private String email;
    private String password;
    private List<String> roles;

}
