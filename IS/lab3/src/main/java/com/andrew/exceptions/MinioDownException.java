package com.andrew.exceptions;

public class MinioDownException extends RuntimeException {
    public MinioDownException() {
        super("MinIO is down");
    }

    public MinioDownException(String message) {
        super(message);
    }
}
