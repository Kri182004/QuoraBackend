package com.quora.quora_backend.service;

import org.springframework.stereotype.Service;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class UserService {
   private final UserRepository userRepository;
   public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
   }
public User registerUser(User user){
    return userRepository.save(user);
}
}
