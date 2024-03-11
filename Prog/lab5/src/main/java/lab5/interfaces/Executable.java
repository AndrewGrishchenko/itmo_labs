package lab5.interfaces;

import lab5.models.ExitCode;

/**
 * Интерфейс для всех выполняемых команд
 */
public interface Executable {
    /**
     * Запуск команды
     * @param args наименование и аргументы команды
     * @return код завершения команды
     * @see ExitCode
     */
    public ExitCode run(String[] args);
}
