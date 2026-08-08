package com.andrew.trig.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andrew.trig.Cosine;
import com.andrew.trig.Sine;
import com.andrew.trig.Tangent;

@ExtendWith(MockitoExtension.class)
public class TangentIntegrationTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    @Mock
    private Sine mockSin;
    @Spy
    private Sine spySin;

    @Mock
    private Cosine mockCos;
    @Spy
    private Cosine spyCos;

    @Test
    void shouldCallSineAndCosineFunction() {
        Tangent tan = new Tangent(spySin, spyCos);
        tan.calculate(new BigDecimal(972), PRECISION);
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    @ParameterizedTest(name = "mock.tan({0}) = {1}")
    @CsvFileSource(resources = "/integration/tanIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCallTangentFunction(BigDecimal x, BigDecimal y) {
        when(mockSin.calculate(eq(x), any()))
            .thenReturn(new BigDecimal(Math.sin(x.doubleValue())));
        when(mockCos.calculate(eq(x), any()))
            .thenReturn(new BigDecimal(Math.cos(x.doubleValue())));

        Tangent tan = new Tangent(mockSin, mockCos);
        assertEquals(y, tan.calculate(x, PRECISION));
    }

    @Test
    void shouldThrowWhenSineIsZero() {
        BigDecimal x = BigDecimal.valueOf(Math.PI).divide(BigDecimal.valueOf(2));
        when(mockSin.calculate(eq(x), any())).thenReturn(BigDecimal.ONE);
        when(mockCos.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO);

        Tangent tan = new Tangent(mockSin, mockCos);
        assertThrows(ArithmeticException.class, () -> tan.calculate(x, PRECISION));
    }
}
