package com.andrew;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TrigonometricTest {
    private static final double EPS = 1e-3;

    @ParameterizedTest(name = "tg({0})")
    @DisplayName("Check stable values")
    @ValueSource(doubles = {
        0.0,
        -0.0,

        Math.PI / 6,
        Math.PI / 4,
        Math.PI / 3,

        -1e-6,
        1e-6
    })
    void checkStableValues(double x) {
        assertAll(
            () -> assertEquals(
                Math.tan(x),
                Trigonometric.tan(x),
                EPS
            )
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {
        Double.NaN,
        Double.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY
    })
    void testSpecialValues(double x) {
        assertEquals(Double.NaN, Trigonometric.tan(x));
    }

    @Test
    void testNormalizationBranches() {
        assertEquals(Math.tan(10), Trigonometric.tan(10), 1e-3);
        assertEquals(Math.tan(-10), Trigonometric.tan(-10), 1e-3);
    }

    @ParameterizedTest(name = "tg({0})")
    @DisplayName("Check asymptotes")
    @ValueSource(doubles = {
        -Math.PI / 2,
        Math.PI / 2,

        Math.PI / 2 - 1e-6,
        -Math.PI / 2 + 1e-6
    })
    void checkAsymptotes(double x) {
        double result = Trigonometric.tan(x);

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return;
        }

        assertTrue(Math.abs(result) > 1e3);
    }

    @ParameterizedTest(name = "tg({0})")
    @DisplayName("Check large values")
    @ValueSource(doubles = {
        1e6,
        -1e6
    })
    void checkLargeValues(double x) {
        double result = Trigonometric.tan(x);

        assertTrue(
            Double.isFinite(result) || Double.isNaN(result)
        );
    }

    @Test
    @DisplayName("Fuzzy testing")
    void fuzzyTest() {
        for (int i = 0; i < 1_000_000; i++) {
            double x = ThreadLocalRandom.current().nextDouble(-1.3, 1.3);

            if (Math.abs(Math.abs(x) - Math.PI / 2) < 0.1) {
                continue;
            }

            assertEquals(
                Math.tan(x),
                Trigonometric.tan(x),
                EPS
            );
        }
    }
}