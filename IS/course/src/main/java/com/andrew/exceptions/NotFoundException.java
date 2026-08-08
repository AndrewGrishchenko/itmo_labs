package com.andrew.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String className, Long id) {
        super(className + " with id " + id + " not found");
    }
}
