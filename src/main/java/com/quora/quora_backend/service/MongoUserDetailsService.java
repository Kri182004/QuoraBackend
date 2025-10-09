package com.quora.quora_backend.service;

import java.util.ArrayList;

import com.quora.quora_backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quora.quora_backend.repository.UserRepository;

@Service
public class MongoUserDetailsService implements UserDetailsService {
    // we use implements hetr because we want to 
    //use the methods defined in the interface UserDetailsService

    private final UserRepository userRepository;
//dependies are injected through the constructor
    public MongoUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User user=userRepository.findByUsername(username)
        .orElseThrow(()->new UsernameNotFoundException("User not found with username: "+username));
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            new ArrayList<>()
        );
       
    }

}
