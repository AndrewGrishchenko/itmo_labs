package lab5.adapters;

import java.util.Scanner;

import lab5.exceptions.TooManyArgumentsException;
import lab5.models.ScanMode;

public class ScannerAdapter {
    private static Scanner userScanner;
    private static ScanMode mode = ScanMode.SINGLE;

    public static Scanner getScanner() {
        return userScanner;
    }

    public static void setScanner(Scanner userScanner) {
        ScannerAdapter.userScanner = userScanner;
    }

    public static String[] getUserInput() {
        return userScanner.nextLine().trim().split(" ");
    }

    public static String getString() {
        return String.join(" ", getUserInput());
    }

    public static Double getDouble() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Double.parseDouble(userInput[0]);
    }

    public static int getInt() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Integer.parseInt(userInput[0]);
    }

    public static Long getLong() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Long.parseLong(userInput[0]);
    }

    public static ScanMode getScanMode() {
        return mode;
    }

    public static void setSingleMode() {
        mode = ScanMode.SINGLE;
    }

    public static void setMultiMode() {
        mode = ScanMode.MULTI;
    }
}
