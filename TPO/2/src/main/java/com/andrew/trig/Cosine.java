package com.andrew.trig;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.andrew.function.AbstractFunction;

import ch.obermuhlner.math.big.BigDecimalMath;

public class Cosine extends AbstractFunction {
    private final Sine sine;

    public Cosine() {
        super();
        this.sine = new Sine();
    }

    public Cosine(Sine sine) {
        super();
        this.sine = sine;
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
        isValid(x, precision);

        MathContext mc = new MathContext(precision.scale() + 2, RoundingMode.HALF_EVEN);

        BigDecimal piHalf = BigDecimalMath.pi(mc)
            .divide(BigDecimal.valueOf(2), mc.getPrecision(), RoundingMode.HALF_EVEN);

        return sine.calculate(piHalf.subtract(x), precision);
    }
}
