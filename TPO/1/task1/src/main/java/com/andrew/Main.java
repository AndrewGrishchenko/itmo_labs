package com.andrew;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java App <x>");
            return;
        }

        try {
            double x = Double.parseDouble(args[0]);

            double result = Trigonometric.tan(x);

            if (Double.isNaN(result)) {
                System.out.println("Result: NaN");
                return;
            }

            System.out.printf("tg(%.6f) = %.6f%n", x, result);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }
}