package com.andrew;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MainTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Path plotsDir = tempDir.resolve("plots");
        try {
            Files.createDirectories(plotsDir);
        } catch (IOException e) {
            throw new RuntimeException("failed to create test directory", e);
        }

        Main.setOutputDir(plotsDir.toString());
    }

    @Test
    @DisplayName("Main should generated CSV files for all functions")
    void shouldGenerateFunctionDataFiles() throws IOException {
        Main.main(new String[] {});

        String[] expectedFiles = {
            "Sine.csv", "Cosine.csv", "Tangent.csv", "Secant.csv",
            "NaturalLogarithm.csv", "BaseNLogarithm.csv",
            "EquationSystem.csv"
        };

        for (String filename : expectedFiles) {
            Path filePath = tempDir.resolve("plots" + File.separator + filename);
            assertTrue(Files.exists(filePath), "Missing file: " + filename);
            assertTrue(Files.size(filePath) > 0, "Empty file: " + filename);
        }
    }

    @Test
    @DisplayName("CSV files should contain valid data")
    void testCsvContent() throws IOException {
        Main.main(new String[] {});

        Path sineFile = tempDir.resolve("plots" + File.separator + "Sine.csv");
        String content = Files.readString(sineFile);

        assertTrue(content.startsWith("x,y"));
    }
}
