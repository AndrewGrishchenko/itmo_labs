package lab7_server.exceptions;

/**
 * Исключение для обработки не найденного id
 */
public class IdNotFoundException extends Exception {
    /**
     * Конструктор исключения
     * @param message сообщение
     */
    public IdNotFoundException(String message) {
        super(message);
    }
}
