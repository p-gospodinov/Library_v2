package com.example.project.error_handling.exception;

public class LibraryOperationException extends RuntimeException{
    public LibraryOperationException(String message) {
        super(message);
    }
}
