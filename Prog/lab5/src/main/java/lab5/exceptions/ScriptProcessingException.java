package lab5.exceptions;

public class ScriptProcessingException extends Exception {
    public ScriptProcessingException(String command) {
        super("Ошибка при выполнении команды '" + command + "'; выполнение скрипта остановлено");
    }
}
