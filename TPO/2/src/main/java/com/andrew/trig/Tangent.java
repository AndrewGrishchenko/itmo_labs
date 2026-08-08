package com.andrew.trig;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.andrew.function.AbstractFunction;

public class Tangent extends AbstractFunction {
    private final Sine sine;
    private final Cosine cosine;

    public Tangent() {
        super();
        this.sine = new Sine();
        this.cosine = new Cosine();
    }

    public Tangent(Sine sine, Cosine cosine) {
        this.sine = sine;
        this.cosine = cosine;
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
        isValid(x, precision);
        
        BigDecimal sin = sine.calculate(x, precision.setScale(precision.scale() + 5, RoundingMode.HALF_EVEN));
        BigDecimal cos = cosine.calculate(x, precision.setScale(precision.scale() + 5, RoundingMode.HALF_EVEN));

        if (cos.abs().compareTo(precision) < 0) {
            throw new ArithmeticException(String.format("tangent doesn't have any value if x = %s", x));
        }

        return sin.divide(cos, precision.scale(), RoundingMode.HALF_EVEN);
    }
}
