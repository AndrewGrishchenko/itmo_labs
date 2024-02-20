package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.models.ExitCode;

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
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }
        
        ConsoleAdapter.println("Завершение программы...");
        return ExitCode.EXIT;
    }
}
