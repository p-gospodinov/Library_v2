package com.example.project.error_handling.exception;

public class LibraryNotFoundException extends RuntimeException{
    public LibraryNotFoundException(String message) {
        super(message);
    }
}
