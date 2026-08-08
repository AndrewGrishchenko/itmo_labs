package com.andrew;

public class Trigonometric {
    public static double tan(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }

        x = reduce(x);

        return sin(x) / cos(x);
    }

    private static double reduce(double x) {
        double pi = Math.PI;
        return (x + pi / 2) % pi - pi / 2;
    }

    private static double sin(double x) {
        double term = x;
        double res = x;

        for (int i = 1; i < 30; i++) {
            term *= -x * x / ((2 * i) * (2 * i + 1));
            res += term;

            if (Math.abs(term) < 1e-15) break;
        }

        return res;
    }

    private static double cos(double x) {
        double term = 1.0;
        double res = 1.0;

        for (int i = 1; i < 30; i++) {
            term *= -x * x / ((2 * i - 1) * (2 * i));
            res += term;

            if (Math.abs(term) < 1e-15) break;
        }

        return res;
    }
}