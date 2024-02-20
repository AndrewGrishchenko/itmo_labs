package lab5.exceptions;

/**
 * Исключение для обработки ошибки во время выполнения скрипта
 */
public class ScriptProcessingException extends Exception {
    /**
     * Конструктор исключения
     * @param command сообщение
     */
    public ScriptProcessingException(String command) {
        super("Ошибка при выполнении команды '" + command + "'");
    }
}
