package com.quora.quora_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder // <-- ADDED
@NoArgsConstructor // <-- ADDED
@AllArgsConstructor // <-- ADDED
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @DBRef(lazy = true)
    @Builder.Default
    private List<Topic> followedTopics = new ArrayList<>();

    // --- ADD ALL THE REQUIRED UserDetails METHODS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert your list of role strings to GrantedAuthority objects
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can add logic for this later
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can add logic for this later
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can add logic for this later
    }

    @Override
    public boolean isEnabled() {
        return true; // You can add logic for this later
    }
}