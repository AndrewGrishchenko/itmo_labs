package lab7_server.commands;

import java.util.ArrayList;

import lab7_server.models.ExitCode;

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
    public String run() {
        String message = "";
        ArrayList<Command> commands = commandManager.getCommands();
        for (int i = 0; i < commands.size(); i++) {
            message += commands.get(i).toString() + "\n";
        }

        return message;
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
