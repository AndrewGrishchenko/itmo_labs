package lab5.utility.console;

public class StandardConsole implements Console {
    public void print(Object obj) {
        System.out.print(obj);
    }

    public void println(Object obj) {
        System.out.println(obj);
    }

    public void printErr(Object obj) {
        println("ОШИБКА: " + obj);
    }

    public void prompt() {
        print("$ ");
    }
}
