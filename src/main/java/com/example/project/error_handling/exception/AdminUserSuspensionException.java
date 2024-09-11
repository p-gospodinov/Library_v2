package com.example.project.error_handling.exception;

public class AdminUserSuspensionException extends RuntimeException{
    public AdminUserSuspensionException(String message) {
        super(message);
    }
}
