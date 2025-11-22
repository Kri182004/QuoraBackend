package com.quora.quora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String userId;
    private String username;
    private String token;
    private String message;
    
    public AuthResponse(String userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }
}