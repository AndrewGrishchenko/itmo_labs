package com.andrew.log;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.andrew.function.AbstractFunction;

public class NaturalLogarithm extends AbstractFunction {
    public NaturalLogarithm() {
        super();
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
        isValid(x, precision);

        if (x.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException(String.format("natural logarithm doesn't have any value if x = %s", x));
        }

        if (x.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal z = x.subtract(BigDecimal.ONE).divide(x.add(BigDecimal.ONE), precision.scale() + 2, RoundingMode.HALF_EVEN);
        BigDecimal z2 = z.pow(2);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = z;
        
        int i = 1;
        do {
            result = result.add(term.divide(BigDecimal.valueOf(i), precision.scale() + 2, RoundingMode.HALF_EVEN));
            term = term.multiply(z2);
            i += 2;
        } while (term.abs().compareTo(precision) > 0 && i < getSeriesLength());

        return result.multiply(BigDecimal.valueOf(2)).setScale(precision.scale(), RoundingMode.HALF_EVEN);
    }
}
