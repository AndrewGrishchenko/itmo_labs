package com.andrew;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.andrew.function.AbstractFunction;
import com.andrew.log.BaseNLogarithm;
import com.andrew.log.NaturalLogarithm;
import com.andrew.trig.Cosine;
import com.andrew.trig.Secant;
import com.andrew.trig.Sine;
import com.andrew.trig.Tangent;

public class EquationSystem extends AbstractFunction {
    private final Sine sin;
    private final Cosine cos;
    private final Tangent tan;
    private final Secant sec;

    private final NaturalLogarithm ln;
    private final BaseNLogarithm log2;
    private final BaseNLogarithm log3;
    private final BaseNLogarithm log5;

    public EquationSystem() {
        super();
        
        sin = new Sine();
        cos = new Cosine();
        tan = new Tangent();
        sec = new Secant();

        ln = new NaturalLogarithm();
        log2 = new BaseNLogarithm(2);
        log3 = new BaseNLogarithm(3);
        log5 = new BaseNLogarithm(5);
    }

    public EquationSystem(Sine sin, Cosine cos, Tangent tan, Secant sec, NaturalLogarithm ln, BaseNLogarithm log2, BaseNLogarithm log3, BaseNLogarithm log5) {
        this.sin = sin;
        this.cos = cos;
        this.tan = tan;
        this.sec = sec;
        this.ln = ln;
        this.log2 = log2;
        this.log3 = log3;
        this.log5 = log5;
    }

    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal precision) {
        final MathContext mc = new MathContext(MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_EVEN);
        final BigDecimal p = precision.setScale(precision.scale() + 10, RoundingMode.HALF_EVEN);

        if (x.compareTo(BigDecimal.ZERO) <= 0) {
            // x <= 0 : (((((sin(x) / tan(x)) - sin(x)) - sec(x)) * (sec(x) ^ 3)) * cos(x))
            try {
                return (
                    ((c(sin, x, p).divide(c(tan, x, p), mc.getPrecision(), RoundingMode.HALF_EVEN)
                        .subtract(c(sin, x, p)))
                        .subtract(c(sec, x, p))
                    ).multiply(
                        (c(sec, x, p).pow(3, mc))
                            .multiply(c(cos, x, p), mc)
                    , mc)
                ).setScale(precision.scale(), RoundingMode.HALF_EVEN);
            } catch (ArithmeticException e) {
                throw new ArithmeticException(String.format("function doesn't have any value if x = %s", x));
            }
        } else {
            // x > 0 : (((((ln(x) + ln(x)) ^ 2) / (log_3(x) - (ln(x) / ln(x)))) + ln(x)) / ((log_2(x) * log_5(x)) - log_2(x)))
            try {
                return (
                    (
                        (
                            c(ln, x, p)
                                .add(c(ln, x, p), mc)
                                .pow(2, mc)
                        ).divide(
                            c(log3, x, p).subtract(
                                c(ln, x, p).divide(c(ln, x, p), mc),
                                mc
                            ),
                            mc
                        ).add(
                            c(ln, x, p),
                            mc
                        )
                    ).divide(
                        (
                            c(log2, x, p).multiply(c(log5, x, p), mc)
                        ).subtract(
                            c(log2, x, p),
                            mc
                        ),
                        mc
                    )
                ).setScale(precision.scale(), RoundingMode.HALF_EVEN);
            } catch (ArithmeticException e) {
                throw new ArithmeticException(String.format("function doesn't have any value if x = %s", x));
            }
        }
    }

    private BigDecimal c(AbstractFunction function, BigDecimal x, BigDecimal precision) {
        return function.calculate(x, precision);
    }
}
