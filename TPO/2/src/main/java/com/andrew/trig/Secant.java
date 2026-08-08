package com.andrew.trig;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.andrew.function.AbstractFunction;

public class Secant extends AbstractFunction {
    private final Cosine cosine;

    public Secant() {
        super();
        this.cosine = new Cosine();
    }

    public Secant(Cosine cosine) {
        super();
        this.cosine = cosine;
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
        isValid(x, precision);

        BigDecimal cos = cosine.calculate(x, precision.setScale(precision.scale() + 12, RoundingMode.HALF_EVEN));

        if (cos.abs().compareTo(precision) < 0) {
            throw new ArithmeticException(String.format("secant doesn't have any value if x = %s", x));
        }

        return BigDecimal.ONE.divide(cos, precision.scale(), RoundingMode.HALF_EVEN);
    }
}
