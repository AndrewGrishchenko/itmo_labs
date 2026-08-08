package com.andrew.trig.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.andrew.trig.Tangent;

public class TangentTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    
    private Tangent tan;

    @BeforeEach
    void init() {
        tan = new Tangent();
    }

    @ParameterizedTest(name = "tan({0})")
    @ValueSource(doubles = { -Math.PI, 0, Math.PI })
    void shouldCalculateForPi(double x) {
        assertEquals(BigDecimal.ZERO.setScale(PRECISION.scale(), RoundingMode.HALF_EVEN), tan.calculate(BigDecimal.valueOf(x), PRECISION));
    }

    @ParameterizedTest
    @ValueSource(doubles = { -Math.PI / 2, Math.PI / 2})
    void shouldNotCalculateForPiHalf(double x) {
        BigDecimal arg = BigDecimal.valueOf(x).setScale(PRECISION.scale(), RoundingMode.HALF_EVEN);
        Throwable exception = assertThrows(ArithmeticException.class, () -> tan.calculate(arg, PRECISION));
        String msg = String.format("tangent doesn't have any value if x = %s", arg);
        assertEquals(msg, exception.getMessage());
    }

    @ParameterizedTest(name = "tan({0})")
    @CsvFileSource(resources = "/tan.csv", numLinesToSkip = 1, delimiter = ',')
    void testTan(BigDecimal x, BigDecimal y) {
        assertEquals(y, tan.calculate(x, PRECISION));
    }
}
