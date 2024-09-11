package com.example.project.security;

import java.util.Base64;

public class KeyGenerator {
    public String generateKey() {
        byte[] randomKey = new byte[32]; // 256 bits
        new java.security.SecureRandom().nextBytes(randomKey);

        // Base64 encode the key
        String secretKey = Base64.getEncoder().encodeToString(randomKey);
        System.out.println("Secret Key: " + secretKey);
        return secretKey;
    }
}
