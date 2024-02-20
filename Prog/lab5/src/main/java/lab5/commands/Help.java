package lab5.commands;

import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CommandManager;
import lab5.models.ExitCode;

/**
 * Команда 'help'. Выводит информацию о доступных командах
 */
public class Help extends Command {
    private CommandManager commandManager;
    
    /**
     * Конструктор команды
     * @param commandManager менеджер команд
     * @see CommandManager
     */
    public Help (CommandManager commandManager) {
        super("help", "вывести справку по доступным командам", "'help'");
        this.commandManager = commandManager;
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
        
        String message = "";
        ArrayList<Command> commands = commandManager.getCommands();
        for (int i = 0; i < commands.size(); i++) {
            message += commands.get(i).toString() + "\n";
        }

        ConsoleAdapter.println(message);
        return ExitCode.OK;
    }
}
