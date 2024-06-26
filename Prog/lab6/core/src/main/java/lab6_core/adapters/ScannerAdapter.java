package lab6_core.adapters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.HashMap;
import java.util.Scanner;

import lab6_core.exceptions.IncompleteScriptRuntimeException;
import lab6_core.exceptions.InvalidDataException;
import lab6_core.exceptions.TooManyArgumentsException;
import lab6_core.models.TicketType;

/**
 * Адаптер для считывания данных
 */
public class ScannerAdapter {
    private static ScanMode currentMode = ScanMode.INTERACTIVE;
    private static HashMap<ScanMode, Scanner> scanners = new HashMap<ScanMode, Scanner>();

    static {
        scanners.put(ScanMode.INTERACTIVE, null);
        scanners.put(ScanMode.FILE, null);
    }

    private static String[] line;
    public static boolean done = false;

    // static {
    //     scanners.put(ScanMode.INTERACTIVE, null);
    //     scanners.put(ScanMode.FILE, null);
    // }

    /**
     * Возвращает текущий используемый сканер
     * @return текущий используемый сканер
     */
    public static Scanner getScanner() {
        return scanners.get(currentMode);
    }

    /**
     * Устанавливает сканер
     * @param scanner сканер
     * @see ScanMode
     */
    public static void addScanner(ScanMode mode, Scanner scanner) {
        scanners.put(mode, scanner);
    }

    public static void setInteractiveScanner (Scanner scanner) {
        scanners.replace(ScanMode.INTERACTIVE, scanner);
    }

    public static void setInteractiveMode () {
        currentMode = ScanMode.INTERACTIVE;
    }

    public static void setFileMode (Scanner scanner) {
        currentMode = ScanMode.FILE;
        scanners.replace(ScanMode.FILE, scanner);
    }

    public static void setFillMode () {
        currentMode = ScanMode.FILL;
    }

    public static void fill (String[] line) {
        ScannerAdapter.line = line;
    }

    /**
     * Проверяет существует ли следующий токен для считывания сканером
     * @return существует ли следующий токен для считывания сканером
     */
    public static boolean hasNext() {
        if (currentMode == ScanMode.FILL) {
            return line != null;
        } else {
            return getScanner().hasNext();
        }
    }

    /**
     * Возвращает считываемый массив строк
     * @return следующий считываемый массив строк 
     */
    public static String[] getUserInput() {
        if (currentMode == ScanMode.INTERACTIVE) {
            if (getScanner().hasNextLine()) {
                String[] line = getScanner().nextLine().trim().split(" ");
                if (line.length == 1 && line[0].equals("")) return null;
                return line;
            }
            else {
                return null;
            }
        } else if (currentMode == ScanMode.FILE) {
            if (hasNext()) {
                String line = getScanner().nextLine();
                if (line.equals("")) return null;
                return new String[] { line };
            } else {
                throw new IncompleteScriptRuntimeException();
            }
        } else {
            return line;
        }
    }

    /**
     * Возвращает следующую считываемую команду
     * @return следующая считываемая команда
     */
    public static String[] getCommand() {
        ConsoleAdapter.prompt();
        String[] userInput = getUserInput();
        if (userInput == null) return new String[]  {""};
        return userInput;
    }

    /**
     * Возвращает следующую считываемую строку
     * @param message сообщение для запроса
     * @return следующая считываемая строка
     */
    public static String getString(String message) {
        ConsoleAdapter.print(message);
        String[] userInput = getUserInput();
        if (userInput == null) return null;
        return String.join(" ", userInput);
    }

    /**
     * Возвращает следующее считываемое число с плавающей точкой (Double)
     * @param message сообщение для запроса
     * @return следующее считываемое число с плавающей точкой (Double)
     */
    public static Double getDouble(String message) {
        while (true) {
            try {
                ConsoleAdapter.print(message);
                String[] userInput = getUserInput();
                if (userInput == null) return null;
                if (userInput.length != 1) throw new TooManyArgumentsException("Слишком много аргументов!");
                return Double.parseDouble(userInput[0]);
            } catch (TooManyArgumentsException e) {
                throw new InvalidDataException(e.getMessage());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Данные должны являться числом!");
            }
        }
    }

    /**
     * Возвращает следующее считываемое число с плавающей точкой (double)
     * @param message сообщение для запроса
     * @return следующее считываемое число с плавающей точкой (double)
     */
    public static double getPrimitiveDouble(String message) {
        while (true) {
            try {
                ConsoleAdapter.print(message);
                String[] userInput = getUserInput();
                if (userInput == null) throw new NullPointerException("невозможно приравнять null к примитивному double!");
                if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");
                return (double) Double.parseDouble(userInput[0]);
            } catch (TooManyArgumentsException | NullPointerException e) {
                throw new InvalidDataException(e.getMessage());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Данные должны являться числом!");
            }
        }
    }

    /**
     * Возвращает следующее считываемое число
     * @param message сообщение для запроса
     * @return следующее считываемое число
     */
    public static int getPrimitiveInt(String message) {
        while (true) {
            try {
                ConsoleAdapter.print(message);
                String[] userInput = getUserInput();
                if (userInput == null) throw new NullPointerException("невозможно приравнять null к примитивному int!");
                if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");
                return Integer.parseInt(userInput[0]);
            } catch (TooManyArgumentsException | NullPointerException e) {
                throw new InvalidDataException(e.getMessage());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Данные должны являться числом!");
            }
        }
    }

    /**
     * Возвращает следующее считываемое число
     * @param message сообщение для запроса
     * @return следующее считываемое число
     */
    public static Long getLong(String message) {
        while (true) {
            try {
                ConsoleAdapter.print(message);
                String[] userInput = getUserInput();
                if (userInput == null) return null;
                if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");
                return Long.parseLong(userInput[0]);
            } catch (TooManyArgumentsException e) {
                throw new InvalidDataException(e.getMessage());
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Данные должны являться числом!");
            }
        }
    }

    /**
     * Возвращает следующую считываемую дату
     * @param message сообщение для запроса
     * @return следующая считываемая дата
     */
    public static ZonedDateTime getZonedDateTime(String message) {
        while (true) {
            try {
                String userInput = getString(message);
                if (userInput == null) return null;
                String[] parts = userInput.split(" ");
                if (parts.length != 3) throw new DateTimeParseException("", userInput, 0);

                LocalDateTime ldt = LocalDateTime.parse(parts[0] + "T" + parts[1]);
                return ldt.atZone(ZoneId.of(parts[2]));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new InvalidDataException("Ошибка формата даты!");
            } catch (ZoneRulesException e) {
                throw new InvalidDataException("Ошибка зоны даты!");
            } 
        }
    }

    /**
     * Возвращает следующее значение типа {@link TicketType}
     * @param message сообщение для запроса
     * @return следующее значение типа {@link TicketType}
     */
    public static TicketType getTicketType(String message) {
        while (true) {
            try {
                String userInput = getString(message);
                if (userInput == null) return null;
                return TicketType.valueOf(userInput);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Введенные данные не являются элементом перечисления!");
            }
        }
    }
}

