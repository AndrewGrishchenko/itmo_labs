package lab5.utility;

import lab5.adapters.ConsoleAdapter;
import lab5.adapters.ScannerAdapter;
import lab5.commands.Command;
import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
import lab5.models.ExitCode;

/**
 * Раннер
 */
public class Runner {
    private final CommandManager commandManager;

    /**
     * Конструктор класса
     * @param collectionManager менеджер коллекции
     * @param commandManager менеджер команд
     * @see CollectionManager
     * @see CommandManager
     */
    public Runner(CollectionManager collectionManager, CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Запуск раннера
     */
    public void run() {
        ExitCode commandStatus;
        String[] userInput;

        do {
            ConsoleAdapter.prompt();
            userInput = ScannerAdapter.getUserInput();
            if (userInput == null) {
                ConsoleAdapter.println("exit");
                commandStatus = launchCommand(new String[] {"exit"});
            } else {
                commandStatus = launchCommand(userInput);
            }
        } while (commandStatus != ExitCode.EXIT);
    }

    /**
     * Запускает команду
     * @param userCommand команда пользователя, в том числе аргументы
     * @return код завершения команды
     */
    private ExitCode launchCommand(String[] userCommand) {
        if (userCommand.length == 1 && userCommand[0] == "") return ExitCode.OK;
        
        Command command = commandManager.getCommand(userCommand[0]);
        if (command == null) {
            ConsoleAdapter.printErr("Неизвестная команда!");
            return ExitCode.ERROR;
        }

        return command.run(userCommand);
    }
}
