package com.andrew.function.module;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.andrew.EquationSystem;

public class EquationSystemTest {
    private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.000001");

    private EquationSystem system;

    @BeforeEach
    void init() {
        system = new EquationSystem();
    }

    @Test
    void shouldNotAcceptNullArg() {
        assertThrows(NullPointerException.class, () -> system.calculate(null, DEFAULT_PRECISION));
    }

    @Test
    void shouldNotAcceptNullPrecision() {
        BigDecimal arg = new BigDecimal(-2);
        assertThrows(NullPointerException.class, () -> system.calculate(arg, null));
    }

    @ParameterizedTest
    @MethodSource("illegalPrecisions")
    void shouldNotAcceptIncorrentPrecisions(BigDecimal precision) {
        assertThrows(ArithmeticException.class, () -> system.calculate(BigDecimal.ONE, precision));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
        -10,
        -1,
        -0.5,
        -Math.PI,
        -2 * Math.PI,
        -0.1
    })
    void shouldCalculateForNegativeDomain(double x) {
        assertDoesNotThrow(() -> system.calculate(BigDecimal.valueOf(x), DEFAULT_PRECISION));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
        0
    })
    void shouldNotAcceptAsymptotes(double x) {
        assertThrows(ArithmeticException.class, () -> system.calculate(BigDecimal.valueOf(x), DEFAULT_PRECISION));
    }

    @ParameterizedTest
    @ValueSource(doubles = {
        0.1,
        0.5,
        1.5,
        2,
        10,
        1000
    })
    void shouldCalculateForPositiveDomain(double x) {
        assertDoesNotThrow(() -> system.calculate(BigDecimal.valueOf(x), DEFAULT_PRECISION));
    }

    @Test
    void shouldNotAcceptZero() {
        assertThrows(ArithmeticException.class, () -> system.calculate(BigDecimal.ZERO, DEFAULT_PRECISION));
    }

    @Test
    void shouldNotAcceptOne() {
        Throwable exception = assertThrows(ArithmeticException.class, () -> system.calculate(BigDecimal.ONE, DEFAULT_PRECISION));
        String msg = String.format("function doesn't have any value if x = %s", 1);
        assertEquals(msg, exception.getMessage());
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/system.csv", numLinesToSkip = 1, delimiter = ',')
    void testSystem(BigDecimal x, BigDecimal y) {
        assertEquals(y, system.calculate(x, DEFAULT_PRECISION));
    }

    private static Stream<Arguments> illegalPrecisions() {
        return Stream.of(
            Arguments.of(BigDecimal.ZERO),
            Arguments.of(BigDecimal.valueOf(1)),
            Arguments.of(BigDecimal.valueOf(-0.01)),
            Arguments.of(BigDecimal.valueOf(1.1))
        );
    }
}
