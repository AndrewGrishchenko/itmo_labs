package com.andrew.trig.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andrew.trig.Cosine;
import com.andrew.trig.Secant;

@ExtendWith(MockitoExtension.class)
public class SecantIntegrationTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    @Mock
    private Cosine mockCos;

    @Spy
    private Cosine spyCos;

    @Test
    void shouldCallCosineFunction() {
        Secant sec = new Secant(spyCos);
        sec.calculate(new BigDecimal(986), PRECISION);
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    @ParameterizedTest(name = "mock.sec({0}) = {1}")
    @CsvFileSource(resources = "/integration/secIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCallSecantFunction(BigDecimal x, BigDecimal y) {
        when(mockCos.calculate(x, PRECISION.setScale(PRECISION.scale() + 12, RoundingMode.HALF_EVEN)))
            .thenReturn(BigDecimal.valueOf(Math.cos(x.doubleValue())));
        Secant sec = new Secant(mockCos);
        assertEquals(y, sec.calculate(x, PRECISION));
    }
}
