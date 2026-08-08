package com.andrew.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andrew.function.AbstractFunction;

@ExtendWith(MockitoExtension.class)
public class CSVGraphWriterTest {
    private static final String DEFAULT_DIR = System.getProperty("user.dir") + File.separator + "plots" + File.separator;

    private CSVGraphWriter writer;

    @Mock
    private AbstractFunction mockFunction;

    @Test
    void shouldCreateFile() throws IOException {
        writer = new CSVGraphWriter(mockFunction, DEFAULT_DIR);
        File expectedFile = new File(DEFAULT_DIR + mockFunction.getClass().getSimpleName() + ".csv");

        assertTrue(expectedFile.exists(), "File must be created");
    }

    @Test
    void shouldWriteToFile() throws IOException {
        when(mockFunction.calculate(any(), any())).thenReturn(BigDecimal.ONE);

        writer = new CSVGraphWriter(mockFunction, DEFAULT_DIR);
        writer.write(BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.valueOf(0.01));

        File file = new File(DEFAULT_DIR + mockFunction.getClass().getSimpleName() + ".csv");
        List<String> lines = Files.readAllLines(file.toPath());

        assertEquals("x,y", lines.get(0));
        assertEquals("0.000000,1.000000", lines.get(1));
    }

    @Test
    void shouldHandleArithmeticException() throws IOException {
        when(mockFunction.calculate(any(), any())).thenThrow(new ArithmeticException("break"));

        writer = new CSVGraphWriter(mockFunction, DEFAULT_DIR);
        writer.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.valueOf(0.01));

        File file = new File(DEFAULT_DIR + mockFunction.getClass().getSimpleName() + ".csv");
        List<String> lines = Files.readAllLines(file.toPath());

        assertTrue(lines.get(1).isEmpty(), "Must be an empty string per break");
        assertEquals("x,y", lines.get(0));
    }

    @Test
    void shouldCallFlush() throws IOException {
        mockFunction = mock(AbstractFunction.class);

        BufferedWriter mockWriter = mock(BufferedWriter.class);

        writer = new CSVGraphWriter(mockWriter, DEFAULT_DIR, mockFunction);
        writer.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.valueOf(0.01));

        verify(mockWriter, atLeastOnce()).flush();
    }

    @AfterEach
    void tearDown() {
        writer = null;
        File file = new File(
            System.getProperty("user.dir") + File.separator + "plots" + File.separator
                + mockFunction.getClass().getSimpleName() + ".csv"
        );

        if (!(file.delete())) {
            System.out.println("File deleted");
        } else {
            System.out.println("File not deleted");
        }
    }
}
