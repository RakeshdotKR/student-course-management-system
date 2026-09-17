package com.scms.exception;

// Custom exception - thrown when a requested entity is not found in the database
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}