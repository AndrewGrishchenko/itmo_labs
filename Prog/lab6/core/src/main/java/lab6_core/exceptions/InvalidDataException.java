package lab6_core.exceptions;

/**
 * Исключение для обработки невалидности данных
 */
public class InvalidDataException extends RuntimeException {
    /**
     * Конструктор исключения
     * @param fieldName имя поля
     */
    public InvalidDataException (String fieldName) {
        super("Поле " + fieldName + " имеет невалидные данные!");
    }
}
