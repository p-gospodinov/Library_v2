package com.example.project.error_handling.exception;

public class EmptyFileException extends RuntimeException{
    public EmptyFileException(String message){
        super(message);
    }
}
