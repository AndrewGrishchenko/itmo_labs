package com.andrew.exceptions;

public class FileException extends RuntimeException {
    public FileException(String fileName) {
        super("Unable to save file \"" + fileName + "\"");
    }
}
