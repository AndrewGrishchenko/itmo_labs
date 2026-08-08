package com.andrew.trig.module;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.andrew.trig.Cosine;
import com.andrew.trig.Sine;

import ch.obermuhlner.math.big.BigDecimalMath;

public class CosineTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    private Cosine cos;

    @BeforeEach
    void init() {
        cos = new Cosine(new Sine());
    }

    @Test
    void shouldCalculateForZero() {
        assertEquals(BigDecimal.ONE.setScale(7, RoundingMode.HALF_EVEN), cos.calculate(BigDecimal.ZERO, PRECISION));
    }

    @Test
    void shouldCalculateForPiHalf() {
        final MathContext mc = new MathContext(MathContext.DECIMAL128.getPrecision());
        final BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_EVEN);
        final BigDecimal expected = BigDecimal.ZERO.setScale(7, RoundingMode.HALF_EVEN);
        
        assertAll(
            () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
            () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION)),
            () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3)), PRECISION)),
            () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3).negate()), PRECISION))
        );
    }

    @Test
    void shouldCalculateForPi() {
        final MathContext mc = new MathContext(MathContext.DECIMAL128.getPrecision());
        final BigDecimal arg = BigDecimalMath.pi(mc);
        final BigDecimal expected = BigDecimal.ONE.setScale(7, RoundingMode.HALF_EVEN).negate();

        assertAll(
            () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
            () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION))
        );
    }

    @ParameterizedTest(name = "cos({0})")
    @CsvFileSource(resources = "/cos.csv", numLinesToSkip = 1, delimiter = ',')
    void testCos(BigDecimal x, BigDecimal y) {
        assertEquals(y, cos.calculate(x, PRECISION));
    }
}
