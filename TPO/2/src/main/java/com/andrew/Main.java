package com.andrew;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.andrew.log.BaseNLogarithm;
import com.andrew.log.NaturalLogarithm;
import com.andrew.trig.Cosine;
import com.andrew.trig.Secant;
import com.andrew.trig.Sine;
import com.andrew.trig.Tangent;
import com.andrew.util.CSVGraphWriter;

public class Main {
    private static String outputDir = System.getProperty("user.dir") + File.separator + "plots" + File.separator;

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private static final BigDecimal POSITIVE_END = new BigDecimal(3).setScale(7, RoundingMode.HALF_EVEN);
    private static final BigDecimal NEGATIVE_END = POSITIVE_END.negate();
    private static final BigDecimal STEP = new BigDecimal("0.001");
    
    public static void main(String[] args) {
        try {
            generateFunctionData();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generateFunctionData() throws IOException {
        new CSVGraphWriter(new Sine(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new Cosine(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new Tangent(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new Secant(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new NaturalLogarithm(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new BaseNLogarithm(2), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new BaseNLogarithm(3), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new BaseNLogarithm(5), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
        new CSVGraphWriter(new EquationSystem(), outputDir).write(NEGATIVE_END, POSITIVE_END, STEP, PRECISION);
    }

    public static void setOutputDir(String path) {
        outputDir = path.endsWith(File.separator) ? path : path + File.separator;
    }
}