package com.quora.quora_backend.filter;

import com.quora.quora_backend.service.JwtService;
import com.quora.quora_backend.service.MongoUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MongoUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, MongoUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // We can add our debug checkpoints back in if needed
        // System.out.println("--- CHECKPOINT 1: JWT AUTH FILTER IS RUNNING ---");
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 1. Check if the request has an Authorization header with a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }



        
        // 2. If we have a token and the user is not yet authenticated for this request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 3. Validate the token against the user details from the database
            if (jwtService.validateToken(token, userDetails)) {
                // If valid, set the user as authenticated in the security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 4. Continue to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}