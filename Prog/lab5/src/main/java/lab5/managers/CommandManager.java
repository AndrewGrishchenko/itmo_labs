package lab5.managers;

import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.commands.Command;
import lab5.models.ExitCode;

/**
 * Менеджер для управления командами
 */
public class CommandManager {
    private final ArrayList<Command> commands = new ArrayList<Command>();

    /**
     * Добавляет команду
     * @param command команда
     * @see Command
     */
    public void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Возвращает список всех команд
     * @return список всех команд
     * @see Command
     */
    public ArrayList<Command> getCommands() {
        return commands;
    }

    /**
     * Возвращает команду по наименованию
     * @param name имя команды
     * @return команда
     * @see Command
     */
    public Command getCommand(String name) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(name)) return commands.get(i);
        }
        return null;
    }

    /**
     * Запускает команду
     * @param args аргументы команды, в том числе имя команды
     * @return код завершения команды
     * @see ExitCode
     */
    public ExitCode invokeCommand(String[] args) {
        Command command = getCommand(args[0]);
        if (command == null) {
            ConsoleAdapter.printErr("команда не найдена!");
            return ExitCode.ERROR;
        }
        return getCommand(args[0]).run(args);
    }
}
