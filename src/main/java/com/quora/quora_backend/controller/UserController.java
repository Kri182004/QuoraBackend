package com.quora.quora_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quora.quora_backend.model.User;
import com.quora.quora_backend.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
    public final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
   @PostMapping("/register")
   public ResponseEntity<User> registerUser(@RequestBody User user){
    User registeredUser=userService.registerUser(user);
    return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
   }
   }

