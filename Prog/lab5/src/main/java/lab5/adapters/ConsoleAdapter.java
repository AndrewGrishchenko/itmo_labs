package lab5.adapters;

public class ConsoleAdapter {
    public static void print(Object obj) {
        System.out.print(obj);
    }

    public static void println(Object obj) {
        System.out.println(obj);
    }

    public static void println() {
        System.out.println();
    }

    public static void printErr(Object obj) {
        println("ОШИБКА: " + obj);
    }

    public static void prompt() {
        print("$ ");
    }

    public static void promptFile() {
        print("> ");
    }
}