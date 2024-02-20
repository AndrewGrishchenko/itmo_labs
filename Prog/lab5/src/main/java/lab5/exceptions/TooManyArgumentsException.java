package lab5.exceptions;

/**
 * Исключение для обработки лишних аргументов
 */
public class TooManyArgumentsException extends Exception {
    /**
     * Конструктор исключения
     * @param message сообщение
     */
    public TooManyArgumentsException(String message) {
        super(message);
    }
}
