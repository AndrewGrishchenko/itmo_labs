package com.andrew.exceptions;

import java.util.Map;

public class ConflictException extends RuntimeException {
    private Map<String, Integer> dependencies;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Map<String, Integer> dependencies) {
        super(message);
        this.dependencies = dependencies;
    }

    public Map<String, Integer> getDependencies() {
        return dependencies;
    }
}
