package com.andrew.log.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.andrew.log.BaseNLogarithm;

public class BaseNLogarithmTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private BaseNLogarithm log5;

    @BeforeEach
    void init() {
        log5 = new BaseNLogarithm(5);
    }

    @Test
    void shouldNotCalculateForZero() {
        assertThrows(ArithmeticException.class, () -> log5.calculate(BigDecimal.ZERO, PRECISION));
    }

    @Test
    void shouldCalculateForOne() {
        assertEquals(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_EVEN), log5.calculate(BigDecimal.ONE, PRECISION));
    }

    @ParameterizedTest(name = "log5({0})")
    @CsvFileSource(resources = "/log5.csv", numLinesToSkip = 1, delimiter = ',')
    void testLog(BigDecimal x, BigDecimal y) {
        assertEquals(y, log5.calculate(x, PRECISION));
    }
}
