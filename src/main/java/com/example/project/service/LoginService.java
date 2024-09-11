package com.example.project.service;

import com.example.project.error_handling.exception.InvalidLoginException;
import com.example.project.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String loginUser(String username, String password){
    try {
        // The authenticationManager will handle user retrieval and password verification
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // If authentication succeeds, generate a token
        return jwtUtil.generateToken(authentication.getName());
    } catch (AuthenticationException e) {
        System.out.println("Authentication failed for user: " + username);
        throw new InvalidLoginException("Invalid login credentials", e);
    }
}
}
