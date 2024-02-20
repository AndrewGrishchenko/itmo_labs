package lab5.exceptions;

/**
 * Исключение для обработки невалидности данных
 */
public class InvalidDataException extends Exception {
    /**
     * Конструктор исключения
     * @param message сообщение
     */
    public InvalidDataException (String message) {
        super(message);
    }
}
