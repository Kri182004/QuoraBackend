package com.quora.quora_backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.UserRegistrationDto;
import com.quora.quora_backend.dto.UserResponseDto;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return userRepository.save(user);
    }

    public UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}