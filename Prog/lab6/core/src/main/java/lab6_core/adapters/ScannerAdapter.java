package lab6_core.adapters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.Scanner;

import lab6_core.exceptions.TooManyArgumentsException;
import lab6_core.models.TicketType;

/**
 * Адаптер для считывания данных
 */
public class ScannerAdapter {
    private static Scanner scanner;

    /**
     * Возвращает текущий используемый сканер
     * @return текущий используемый сканер
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Устанавливает сканер
     * @param scanner сканер
     * @see ScanMode
     */
    public static void setScanner(Scanner scanner) {
        ScannerAdapter.scanner = scanner;
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
        if (getScanner().hasNextLine()) {
            String[] line = getScanner().nextLine().trim().split(" ");
            if (line.length == 1 && line[0].equals("")) return null;
            return line;
        }
        else {
            return null;
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
                if (userInput.length != 1) throw new TooManyArgumentsException("слишком много аргументов!");
                return Double.parseDouble(userInput[0]);
            } catch (TooManyArgumentsException e) {
                ConsoleAdapter.printErr(e.getMessage());
            } catch (NumberFormatException e) {
                ConsoleAdapter.printErr("данные должны являться числом!");
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
                ConsoleAdapter.printErr(e.getMessage());
            } catch (NumberFormatException e) {
                ConsoleAdapter.printErr("данные должны являться числом!");
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
                ConsoleAdapter.printErr(e.getMessage());
            } catch (NumberFormatException e) {
                ConsoleAdapter.printErr("данные должны являться числом!");
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
                ConsoleAdapter.printErr(e.getMessage());
            } catch (NumberFormatException e) {
                ConsoleAdapter.printErr("данные должны являться числом!");
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
                ConsoleAdapter.printErr("ошибка формата даты!");
            } catch (ZoneRulesException e) {
                ConsoleAdapter.printErr("ошибка зоны даты!");
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
                ConsoleAdapter.printErr("введенные данные не являются элементом перечисления!");
            }
        }
    }
}

