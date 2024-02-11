package lab5.adapters;

import java.util.Scanner;

public class ScannerAdapter {
    private static Scanner userScanner;

    public static Scanner getScanner() {
        return userScanner;
    }

    public static void setScanner(Scanner userScanner) {
        ScannerAdapter.userScanner = userScanner;
    }

    public static String[] getUserInput() {
        Scanner scanner = getScanner();
        return scanner.nextLine().trim().split(" ", 2);
    }
}
