package com.andrew.function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.andrew.log.BaseNLogarithm;
import com.andrew.log.NaturalLogarithm;
import com.andrew.trig.Cosine;
import com.andrew.trig.Secant;
import com.andrew.trig.Tangent;

public class FunctionRuleTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.000001");
    private static final BigDecimal NEGATIVE_PRECISION = PRECISION.negate();
    private static final BigDecimal POSITIVE_PRECISION = PRECISION.add(BigDecimal.ONE);

    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptNullArg(final FunctionRule function) {
        Throwable exception = assertThrows(NullPointerException.class, () -> function.calculate(null, PRECISION));
        assertEquals("argument must not be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptNullPrecision(final FunctionRule function) {
        Throwable exception = assertThrows(NullPointerException.class, () -> function.calculate(BigDecimal.ONE, null));
        assertEquals("precision must not be null", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("functions")
    void shouldNotAcceptOutsideZeroAndOne(final FunctionRule function) {
        assertAll(
            () -> {
                Throwable exception = assertThrows(ArithmeticException.class,
                    () -> function.calculate(BigDecimal.ONE, NEGATIVE_PRECISION));
                assertEquals("precision must be within [0; 1] range", exception.getMessage());
            },
            () -> {
                Throwable exception = assertThrows(ArithmeticException.class,
                    () -> function.calculate(BigDecimal.ONE, POSITIVE_PRECISION));
                assertEquals("precision must be within [0; 1] range", exception.getMessage());
            }
        );
    }

    @ParameterizedTest
    @MethodSource("functions")
    void shouldAcceptArgAndPrecision(final FunctionRule function) {
        assertDoesNotThrow(() -> function.calculate(BigDecimal.ONE, PRECISION));
    }

    private static Stream<Arguments> functions() {
        return Stream.of(
            Arguments.of(new Cosine()),
            Arguments.of(new Tangent()),
            Arguments.of(new Secant()),
            Arguments.of(new NaturalLogarithm()),
            Arguments.of(new BaseNLogarithm(2)),
            Arguments.of(new BaseNLogarithm(3)),
            Arguments.of(new BaseNLogarithm(5))
        );
    }
}
