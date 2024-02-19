package lab5.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import lab5.adapters.ScannerAdapter;
import lab5.exceptions.IncompleteScriptRuntimeException;
import lab5.exceptions.ScriptProcessingException;
import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
import lab5.models.ScanMode;
import lab5.utility.console.Console;

public class ExecuteScript extends Command {
    private Console console;
    private CollectionManager collectionManager;
    private CommandManager commandManager;
    
    public ExecuteScript (Console console, CollectionManager collectionManager, CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме", "'execute_script <fileName>'");
        this.console = console;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            console.println(getUsage());
            return false;
        }

        try {
            String fileName = args[1];

            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            // ScannerAdapter.addScanner(ScanMode.FILE, new Scanner(inputStreamReader));
            ScannerAdapter.setFileMode(inputStreamReader);
            // Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
            // String commands = scanner.hasNext() ? scanner.next() : "";
            
            // String[] some = ScannerAdapter.getUserInput();
            // for (int i = 0; i < some.length; i++) {
            //     System.out.println(some[i]);
            // }
            
            String[] userInput;
            while (ScannerAdapter.hasNext()) {
                userInput = ScannerAdapter.getUserInput()[0].split(" ");
                
                // console.println("invoke " + userInput);
                console.print("invoking: ");
                for (int i = 0; i < userInput.length; i++) {
                    console.print(userInput[i]);
                    console.print(" ");
                }
                console.println("");

                if (!commandManager.invokeCommand(userInput)) {
                    throw new ScriptProcessingException(userInput[0]);
                }
            }
            

            ScannerAdapter.setInteractiveMode();

            // scanner.close();
            // inputStreamReader.close();
            // fileInputStream.close();
        } catch (FileNotFoundException e) {
            console.printErr("файл не найден!");
        } catch (IOException e) {
            console.printErr("ошибка ввода/вывода!");
        } catch (ScriptProcessingException e) {
            console.printErr(e.getMessage());
        } catch (IncompleteScriptRuntimeException e) {
            console.printErr(e.getMessage());
        }

        ScannerAdapter.setInteractiveMode();
        return false;
    }
}
