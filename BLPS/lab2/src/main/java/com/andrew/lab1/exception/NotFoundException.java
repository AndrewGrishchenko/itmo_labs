package com.andrew.lab1.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String object, Long id) {
        super(object + "with id " + String.valueOf(id) + " not found");
    }
}
