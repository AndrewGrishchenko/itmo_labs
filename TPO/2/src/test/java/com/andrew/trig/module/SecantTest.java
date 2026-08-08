package com.andrew.trig.module;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.andrew.trig.Secant;

import ch.obermuhlner.math.big.BigDecimalMath;

public class SecantTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    
    private Secant sec;

    @BeforeEach
    void init() {
        sec = new Secant();
    }

    @Test
    void shouldCalculateForMinimum() {
        assertEquals(BigDecimal.ONE.setScale(7, RoundingMode.HALF_EVEN), sec.calculate(BigDecimal.ZERO, PRECISION));
    }

    @Test
    void shouldCalculateForMaximum() {
        MathContext mc = new MathContext(7, RoundingMode.HALF_EVEN);
        assertAll(
            () -> assertEquals(BigDecimal.ONE.negate().setScale(7, RoundingMode.HALF_EVEN),
                sec.calculate(BigDecimalMath.pi(mc).negate(), PRECISION)),
            () -> assertEquals(BigDecimal.ONE.negate().setScale(7, RoundingMode.HALF_EVEN),
                sec.calculate(BigDecimalMath.pi(mc), PRECISION))
        );
    }

    @Test
    void shouldNotCalculateForPiHalf() {
        MathContext mc = new MathContext(MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_EVEN);
        BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), mc);
        BigDecimal negativeArg = arg.negate();
        
        assertAll(
            () -> {
                Throwable exception = assertThrows(ArithmeticException.class, () -> sec.calculate(negativeArg, PRECISION));
                String msg = String.format("secant doesn't have any value if x = %s", negativeArg);
                assertEquals(msg, exception.getMessage());
            },
            () -> {
                Throwable exception = assertThrows(ArithmeticException.class, () -> sec.calculate(arg, PRECISION));
                String msg = String.format("secant doesn't have any value if x = %s", arg);
                assertEquals(msg, exception.getMessage());
            }
        );
    }

    @ParameterizedTest(name = "sec({0})")
    @CsvFileSource(resources = "/sec.csv", numLinesToSkip = 1, delimiter = ',')
    void testSec(BigDecimal x, BigDecimal y) {
        assertEquals(y, sec.calculate(x, PRECISION));
    }
}
