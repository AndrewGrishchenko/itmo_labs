package lab5.adapters;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import lab5.exceptions.IncompleteScriptRuntimeException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.models.ScanMode;

/**
 * Адаптер для считывания данных
 */
public class ScannerAdapter {
    private static ScanMode currentMode = ScanMode.INTERACTIVE;
    private static HashMap<ScanMode, Scanner> scanners = new HashMap<>();

    static {
        scanners.put(ScanMode.INTERACTIVE, new Scanner(System.in));
        scanners.put(ScanMode.FILE, new Scanner(System.in));
    }

    /**
     * Возвращает текущий используемый сканер
     * @return текущий используемый сканер
     */
    public static Scanner getScanner() {
        return scanners.get(currentMode);
    }

    /**
     * Добавляет сканнер в список сканнеров
     * @param mode ключ для сканнера
     * @param scanner сканнер
     * @see ScanMode
     */
    public static void addScanner(ScanMode mode, Scanner scanner) {
        scanners.put(mode, scanner);
    }

    /**
     * Проверяет существует ли следующий токен для считывания сканером
     * @return существует ли следующий токен для считывания сканером
     */
    public static boolean hasNext() {
        return getScanner().hasNext();
    }

    /**
     * Возвращает считываемый массив строк
     * @return следующий считываемый массив строк 
     */
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

    /**
     * Возвращает следующую считываемую строку
     * @return следующая считываемая строка
     */
    public static String getString() {
        return String.join(" ", getUserInput());
    }

    /**
     * Возвращает следующее считываемое число с плавающей точкой
     * @return следующее считываемое число с плавающей точкой
     * @throws TooManyArgumentsException возникает при наличии больше одного аргумента пользовательского ввода
     */
    public static Double getDouble() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Double.parseDouble(userInput[0]);
    }

    /**
     * Возвращает следующее считываемое число
     * @return следующее считываемое число
     * @throws TooManyArgumentsException возникает при наличии больше одного аргумента пользовательского ввода
     */
    public static int getInt() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Integer.parseInt(userInput[0]);
    }

    /**
     * Возвращает следующее считываемое число
     * @return следующее считываемое число
     * @throws TooManyArgumentsException возникает при наличии больше одного аргумента пользовательского ввода
     */
    public static Long getLong() throws TooManyArgumentsException {
        String[] userInput = getUserInput();
        if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");

        return Long.parseLong(userInput[0]);
    }

    /**
     * Возвращает текущий режим сканера
     * @return текущий режим сканера
     * @see ScanMode
     */
    public static ScanMode getScanMode() {
        return currentMode;
    }

    /**
     * Устанавливает режим сканера - интерактивный
     */
    public static void setInteractiveMode() {
        currentMode = ScanMode.INTERACTIVE;
    }

    /**
     * Устанавливает режим сканера - файл
     * @param isr InputStreamReader файла
     */
    public static void setFileMode(InputStreamReader isr) {
        currentMode = ScanMode.FILE;
        scanners.replace(currentMode, new Scanner(isr));
    }
}
