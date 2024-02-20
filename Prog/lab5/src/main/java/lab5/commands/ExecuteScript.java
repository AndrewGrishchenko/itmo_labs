package lab5.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import lab5.adapters.ConsoleAdapter;
import lab5.adapters.ScannerAdapter;
import lab5.exceptions.IncompleteScriptRuntimeException;
import lab5.exceptions.ScriptProcessingException;
import lab5.managers.CommandManager;

public class ExecuteScript extends Command {
    private CommandManager commandManager;
    
    public ExecuteScript (CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме", "'execute_script <fileName>'");
        this.commandManager = commandManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(getUsage());
            return false;
        }

        try {
            String fileName = args[1];

            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            ScannerAdapter.setFileMode(inputStreamReader);
            
            String[] userInput;
            while (ScannerAdapter.hasNext()) {
                userInput = ScannerAdapter.getUserInput()[0].split(" ");

                if (!commandManager.invokeCommand(userInput)) {
                    throw new ScriptProcessingException(userInput[0]);
                }

                if (userInput[0] == "exit") return true;
            }
            
            ScannerAdapter.setInteractiveMode();
            return true;
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("файл не найден!");
        } catch (ScriptProcessingException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (IncompleteScriptRuntimeException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        ScannerAdapter.setInteractiveMode();
        return false;
    }
}
