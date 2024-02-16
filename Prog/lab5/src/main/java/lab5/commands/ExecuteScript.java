package lab5.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
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

            Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
            String commands = scanner.hasNext() ? scanner.next() : "";
            
            scanner.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            console.printErr("файл не найден!");
        } catch (IOException e) {
            console.printErr("ошибка ввода/вывода!");
        } 

        return false;
    }
}
