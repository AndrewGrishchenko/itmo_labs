package com.andrew.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.andrew.exceptions.FileException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileStorageService {
    private final Path uploadDir = Paths.get("/var/lighthouse/uploads");

    public String save(InputStream stream, String originalName) {
        try {
            Files.createDirectories(uploadDir);

            String uniqueName = UUID.randomUUID() + "_" + sanitize(originalName);
            Path target = uploadDir.resolve(uniqueName);

            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);

            return uniqueName;
        } catch (IOException e) {
            throw new FileException(originalName);
        }
    }

    public Path resolve(String filePath) {
        return Paths.get(uploadDir.toString(), filePath);
    }

    private String sanitize(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}
