package com.example.project.error_handling.exception;

public class InactiveUserException extends RuntimeException{
    public InactiveUserException(String message) {
        super(message);
    }

}
