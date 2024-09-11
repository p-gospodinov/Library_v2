package com.example.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final PasswordEncoder passwordEncoder;

    public TestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

//    @GetMapping("/test-password")
//    public String testPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
//        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
//        return matches ? "Password matches" : "Password does not match";
//    }
@PostMapping("/test-password")
public String testPassword(@RequestBody Map<String, String> payload) {
    String rawPassword = payload.get("rawPassword");
    String encodedPassword = payload.get("encodedPassword");
    boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
    return matches ? "Password matches" : "Password does not match";
}

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            // Example validation
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String contentType = file.getContentType();
            if (!"application/pdf".equals(contentType) && !"text/plain".equals(contentType)) {
                return ResponseEntity.badRequest().body("Only PDF or TXT files are allowed");
            }

            // Example processing
            byte[] fileData = file.getBytes();
            String fileName = file.getOriginalFilename();
            // Process file data here

            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
