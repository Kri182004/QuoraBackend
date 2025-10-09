package com.quora.quora_backend.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.dto.UserRegistrationDto;
import com.quora.quora_backend.dto.UserResponseDto;
import com.quora.quora_backend.model.User;
import com.quora.quora_backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder =passwordEncoder;
    }

    public User registerUser(UserRegistrationDto userDto){
        //check username if already exist
        if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        //encode the password before saving it
        String hashedPassword=passwordEncoder.encode(userDto.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}