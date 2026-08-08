package com.andrew.lab1.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.exception.ForbiddenException;
import com.andrew.lab1.exception.ValidationException;

@RestControllerAdvice
public class ExceptionMapper {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "not_found");
        body.put("message", ex.getMessage());

        return body;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(ValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "validation_error");
        body.put("message", ex.getMessage());

        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "validation_error");
        body.put("message", ex.getMessage());

        return body;
    }

    @ExceptionHandler(ForbiddenException.class)
    public Map<String, Object> handleOwnership(ForbiddenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "ownership_error");
        body.put("message", ex.getMessage());

        return body;
    }
}
