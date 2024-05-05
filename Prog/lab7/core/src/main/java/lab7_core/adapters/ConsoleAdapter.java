package lab7_core.adapters;

/**
 * Адаптер для вывода в консоль
 */
public class ConsoleAdapter {
    /**
     * Выводит obj.toString() в консоль
     * @param obj объект для печати
     */
    public static void print(Object obj) {
        if (obj == null) return;
        System.out.print(obj);
    }

    /**
     * Выводит obj.toString() + \n в консоль
     * @param obj объект для печати
     */
    public static void println(Object obj) {
        if (obj == null) return;
        System.out.println(obj);
    }

    /**
     * Выводит пустую строку в консоль
     */
    public static void println() {
        System.out.println();
    }

    /**
     * Выводит obj.toString() как ошибку
     * @param obj объект для печати
     */
    public static void printErr(Object obj) {
        println("ОШИБКА: " + obj);
    }

    /**
     * Выводит символ $ 
     */
    public static void prompt() {
        print("$ ");
    }

    /**
     * Выводит символ > 
     */
    public static void promptFile() {
        print("> ");
    }
}
