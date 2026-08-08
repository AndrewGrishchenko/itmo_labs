package com.andrew.function;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class AbstractFunction implements FunctionRule {
    private static final int MAX_ITERATIONS = 1000;

    protected final int seriesLength;

    protected AbstractFunction() {
        this.seriesLength = MAX_ITERATIONS;
    }

    protected void isValid(final BigDecimal x, final BigDecimal precision) {
        Objects.requireNonNull(x, "argument must not be null");
        Objects.requireNonNull(precision, "precision must not be null");
        if (precision.compareTo(BigDecimal.ZERO) <= 0 || precision.compareTo(BigDecimal.ONE) >= 0) {
            throw new ArithmeticException("precision must be within [0; 1] range");
        }
    }

    public int getSeriesLength() {
        return seriesLength;
    }
}
