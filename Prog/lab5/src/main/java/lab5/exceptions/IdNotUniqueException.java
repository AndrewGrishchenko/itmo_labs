package lab5.exceptions;

/**
 * Исключение для обработки неуникальности id
 */
public class IdNotUniqueException extends Exception {
    /**
     * Конструктор исключения
     * @param message сообщение
     */
    public IdNotUniqueException(String message) {
        super(message);
    }
}
