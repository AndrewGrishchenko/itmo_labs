package lab6_server.commands;

import lab6_server.models.ExitCode;

/**
 * Команда 'exit'. Завершает выполнение программы без сохранения коллекции
 */
public class Exit extends Command {
    /**
     * Конструктор команды
     */
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)", "'exit'");
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String invoke() {
        return "Завершение программы...";
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null; 
    }
}
