package com.example.project.error_handling.exception;

public class BookOperationException extends RuntimeException{
    public BookOperationException(String message){
        super(message);
    }
}
