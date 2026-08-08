package com.andrew.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import com.andrew.function.AbstractFunction;
import com.andrew.log.BaseNLogarithm;

public class CSVGraphWriter {
    private final BufferedWriter writer;
    private final AbstractFunction function;
    private final String filePath;

    public CSVGraphWriter(AbstractFunction function, String outputDir) throws IOException {
        this.function = function;
        this.filePath = getFilePath(outputDir, function);
        this.writer = createWriter();
    }

    public CSVGraphWriter(BufferedWriter writer, String outputDir, AbstractFunction function) {
        this.function = function;
        this.filePath = getFilePath(outputDir, function);
        this.writer = writer;
    }

    private String getFilePath(String outputDir, AbstractFunction function) {
        if (function.getClass().equals(BaseNLogarithm.class)) {
            BaseNLogarithm logFunction = (BaseNLogarithm) function;
            return outputDir + function.getClass().getSimpleName() + logFunction.getBase() + ".csv"; 
        } else {
            return outputDir + function.getClass().getSimpleName() + ".csv";
        }
    }

    private BufferedWriter createWriter() throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            return new BufferedWriter(new FileWriter(file, false));
        } else {
            file.createNewFile();
            return new BufferedWriter(new FileWriter(file));
        }
    }

    public void write(BigDecimal x1, BigDecimal x2, BigDecimal d, BigDecimal precision) throws IOException {
        try {
            writer.write("x,y");
            writer.newLine();
            for (BigDecimal i = x1; i.compareTo(x2) <= 0; i = i.add(d)) {
                try {
                    BigDecimal y = function.calculate(i, precision);
                    if (y != null) {
                        writer.write(String.format("%f,%f%n", i.doubleValue(), y.doubleValue()));
                    } else {
                        writer.newLine();
                    }
                } catch (ArithmeticException e) {
                    writer.newLine();
                }
            }
        } finally {
            writer.flush();
        }
    }
}
