package lab5.adapters;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import lab5.exceptions.IncompleteScriptRuntimeException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.models.ScanMode;

public class ScannerAdapter {;
    private static ScanMode currentMode = ScanMode.INTERACTIVE;
    private static HashMap<ScanMode, Scanner> scanners = new HashMap<>();

    static {
        scanners.put(ScanMode.INTERACTIVE, new Scanner(System.in));
        scanners.put(ScanMode.FILE, new Scanner(System.in));
    }

    public static Scanner getScanner() {
        return scanners.get(currentMode);
    }

    public static void addScanner(ScanMode mode, Scanner scanner) {
        scanners.put(mode, scanner);
    }

    public static boolean hasNext() {
        return getScanner().hasNext();
    }

    public static String[] getUserInput() {
        if (currentMode == ScanMode.INTERACTIVE) {
            if (getScanner().hasNextLine()) {
                return getScanner().nextLine().trim().split(" ");
            }
            else {
                return null;
            }
        }
        else {
            if (hasNext()) {
                String line = getScanner().nextLine();
                ConsoleAdapter.promptFile();
                ConsoleAdapter.println(line);
                return new String[] { line };
            }
            else {
                throw new IncompleteScriptRuntimeException();
            }
        }
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
        return currentMode;
    }

    public static void setInteractiveMode() {
        currentMode = ScanMode.INTERACTIVE;
    }

    public static void setFileMode(InputStreamReader isr) {
        currentMode = ScanMode.FILE;
        scanners.replace(currentMode, new Scanner(isr));
    }
}
