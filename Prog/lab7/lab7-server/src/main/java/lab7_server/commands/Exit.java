package lab7_server.commands;

import lab7_server.models.ExitCode;

/**
 * Команда 'exit'. Завершает выполнение программы без сохранения коллекции
 */
public class Exit extends Command {
    /**
     * Конструктор команды
     */
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)", "'exit'", false, 1);
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        return "Завершение программы...";
    }
}
