package lab7_server.exceptions;

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
