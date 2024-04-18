package lab6_core.exceptions;

/**
 * Исключение для обработки незаконченного скрипта
 */
public class IncompleteScriptRuntimeException extends RuntimeException {
    /**
     * Конструктор исключения
     * @param message сообщение
     */
    public IncompleteScriptRuntimeException (String message) {
        super(message);
    }

    /**
     * Конструктор исключения с сообщением "неполный скрипт"
     */
    public IncompleteScriptRuntimeException() {
        super("неполный скрипт");
    }
}