package com.andrew.log.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.andrew.log.NaturalLogarithm;

public class NaturalLogarithmTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.000001");

    private NaturalLogarithm ln;

    @BeforeEach
    void init() {
        ln = new NaturalLogarithm();
    }

    @Test
    void shouldNotCalculateForZero() {
        assertThrows(ArithmeticException.class, () -> ln.calculate(BigDecimal.ZERO, PRECISION));
    }

    @Test
    void shouldCalculateForOne() {
        assertEquals(BigDecimal.ZERO, ln.calculate(BigDecimal.ONE, PRECISION));
    }

    @ParameterizedTest(name = "ln({0})")
    @ValueSource(doubles = { 0.5, 0.707, 1.2, 2.0, 2.2 })
    void testLn(double d) {
        BigDecimal expected = BigDecimal.valueOf(Math.log(d)).setScale(6, RoundingMode.HALF_EVEN);
        assertEquals(expected, ln.calculate(BigDecimal.valueOf(d), PRECISION));
    }
}
