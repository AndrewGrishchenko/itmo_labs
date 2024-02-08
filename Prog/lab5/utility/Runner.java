package utility;

import adapters.ScannerAdapter;
import commands.Command;
import managers.CommandManager;
import utility.console.Console;

public class Runner {
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
    }

    private final Console console;
    private final CommandManager commandManager;

    public Runner(Console console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void interactiveMode() {
        try {
            ExitCode commandStatus;
            String[] userInput;

            do {
                console.prompt();
                userInput = ScannerAdapter.getUserInput();
                commandStatus = launchCommand(userInput);
            } while (commandStatus != ExitCode.EXIT);

        } catch (Exception e) {
            console.printErr(e.getMessage());
        }
    }

    private ExitCode launchCommand(String[] userCommand) {
        if (userCommand.length == 1 && userCommand[0] == "") return ExitCode.OK;
        
        Command command = commandManager.getCommand(userCommand[0]);
        if (command == null) {
            console.printErr("Неизвестная команда!");
            return ExitCode.ERROR;
        }

        switch (userCommand[0]) {
            case "exit":
                command.run();
                return ExitCode.EXIT;
            default:
                if (!command.run()) return ExitCode.ERROR;
                break;
        }
        
        return ExitCode.OK;
    }
}
