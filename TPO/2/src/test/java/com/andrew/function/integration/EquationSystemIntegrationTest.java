package com.andrew.function.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andrew.EquationSystem;
import com.andrew.log.BaseNLogarithm;
import com.andrew.log.NaturalLogarithm;
import com.andrew.trig.Cosine;
import com.andrew.trig.Secant;
import com.andrew.trig.Sine;
import com.andrew.trig.Tangent;

@ExtendWith(MockitoExtension.class)
public class EquationSystemIntegrationTest {
    private static final BigDecimal PRECISION = new BigDecimal("0.000001");

    @Spy
    private Sine spySin;
    @Spy
    private Cosine spyCos;
    @Spy
    private Tangent spyTan;
    @Spy
    private Secant spySec;
    @Spy
    private NaturalLogarithm spyLn;
    @Spy
    private BaseNLogarithm spyLog2;
    @Spy
    private BaseNLogarithm spyLog3;
    @Spy
    private BaseNLogarithm spyLog5;

    @Mock
    private Sine mockSin;
    @Mock
    private Cosine mockCos;
    @Mock
    private Tangent mockTan;
    @Mock
    private Secant mockSec;
    @Mock
    private NaturalLogarithm mockLn;
    @Mock
    private BaseNLogarithm mockLog2;
    @Mock
    private BaseNLogarithm mockLog3;
    @Mock
    private BaseNLogarithm mockLog5;

    @Test
    void shouldCallAllTrigFunctions() {
        EquationSystem system = new EquationSystem(spySin, spyCos, spyTan, spySec, spyLn, spyLog2, spyLog3, spyLog5);
        system.calculate(new BigDecimal(-5), new BigDecimal("0.0001"));
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyTan, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spySec, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verifyNoInteractions(spyLn);
        verifyNoInteractions(spyLog2);
        verifyNoInteractions(spyLog3);
        verifyNoInteractions(spyLog5);
    }

    @Test
    void shouldCallAllLogFunctions() {
        EquationSystem system = new EquationSystem(spySin, spyCos, spyTan, spySec, spyLn, spyLog2, spyLog3, spyLog5);
        system.calculate(new BigDecimal(5), new BigDecimal("0.0001"));
        verifyNoInteractions(spySin);
        verifyNoInteractions(spyCos);
        verifyNoInteractions(spyTan);
        verifyNoInteractions(spySec);
        verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyLog2, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyLog3, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
        verify(spyLog5, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/integration/systemIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateWithMockFunctions(BigDecimal x, BigDecimal y) {
        if (x.compareTo(BigDecimal.ZERO) > 0) {
            when(mockLn.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue())));
            when(mockLog2.calculate(eq(x), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(2)));
            when(mockLog3.calculate(eq(x), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(3)));
            when(mockLog5.calculate(eq(x), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(5)));
        } else {
            when(mockSin.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.sin(x.doubleValue())));
            when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.cos(x.doubleValue())));
            when(mockTan.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.tan(x.doubleValue())));
            when(mockSec.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(1 / Math.cos(x.doubleValue())));
        }
        EquationSystem system = new EquationSystem(mockSin, mockCos, mockTan, mockSec, mockLn, mockLog2, mockLog3, mockLog5);
        assertEquals(y, system.calculate(x, PRECISION));
    }
}
