package com.example.project.controller;

import com.example.project.enums.BookStatus;
import com.example.project.model.Book;
import com.example.project.security.JwtUtil;
import com.example.project.security.PasswordEncoderService;
import com.example.project.service.CustomUserDetailsService;
import com.example.project.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/authenticate")
@Tag(name = "Authentication Controller", description = "Operations related to authentication")
public class AuthController {
    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    @Operation(summary = "Login", description = "Jwt authentication demo")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        String token = loginService.loginUser(username, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil jwtUtil;
//    private final CustomUserDetailsService customUserDetailsService;
//    private final PasswordEncoder passwordEncoder;
//   public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUtil = jwtUtil;
//        this.customUserDetailsService = customUserDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    @PostMapping("/authenticate")
//    public String authenticate(@RequestParam String username, @RequestParam String password) {
////        try {
////            System.out.println("Attempting to authenticate username: " + username + "and password:" + password);
////            Authentication authentication = authenticationManager.authenticate(
////                    new UsernamePasswordAuthenticationToken(username, password)
////            );
////            System.out.println("Authentication successful for user: " + username);
////            return jwtUtil.generateToken(authentication.getName());
////        } catch (AuthenticationException e) {
////            System.out.println("Authentication failed for user: " + username);
////            throw new RuntimeException("Invalid login credentials", e);
////        }
//        try {
//            System.out.println("Attempting to authenticate user: " + username);
//
//            // Fetch the user manually
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);;
//
//            // Manually check if the password matches
//            boolean matches = passwordEncoder.matches(password, userDetails.getPassword());
//            System.out.println("pass: " + password + "database pass: "+ userDetails.getPassword());
//            System.out.println("Password matches: " + matches);
//
//            if (!matches) {
//                throw new RuntimeException("Password does not match");
//            }
//
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
//
//            // Proceed with the regular authentication process
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(username, password)
//            );
//            return jwtUtil.generateToken(authentication.getName());
//        } catch (AuthenticationException e) {
//            System.out.println("Authentication failed for user: " + username);
//            throw new RuntimeException("Invalid login credentials", e);
//        }
//    }

