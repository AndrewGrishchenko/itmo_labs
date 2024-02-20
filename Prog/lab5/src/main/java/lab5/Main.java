package lab5;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import lab5.adapters.ConsoleAdapter;
import lab5.adapters.ScannerAdapter;
import lab5.commands.*;
import lab5.exceptions.InvalidDataException;
import lab5.managers.CollectionManager;
import lab5.managers.CommandManager;
import lab5.models.ScanMode;
import lab5.utility.Runner;

/**
 * Главный класс
 */
public class Main {
    /**
     * Главный метод
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        final CollectionManager collectionManager = new CollectionManager();
        ScannerAdapter.addScanner(ScanMode.INTERACTIVE, new Scanner(System.in));

        if (args.length == 0) {
            ConsoleAdapter.printErr("не указано имя файла!");
            return;
        }

        final String fileName = args[0];
        try {
            collectionManager.dumpData(fileName);
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("файл не найден!");
        } catch (IOException e) {
            ConsoleAdapter.printErr("ошибка ввода/вывода!");
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        CommandManager commandManager = new CommandManager() {{
            addCommand(new Exit());
            addCommand(new Show(collectionManager));
            addCommand(new Clear(collectionManager));
            addCommand(new RemoveKey(collectionManager));
            addCommand(new Save(collectionManager, fileName));
            addCommand(new Insert(collectionManager));
            addCommand(new Update(collectionManager));
            addCommand(new RemoveLower(collectionManager));
            addCommand(new ReplaceIfLower(collectionManager));
            addCommand(new RemoveLowerKey(collectionManager));
            addCommand(new RemoveAnyByEvent(collectionManager));
            addCommand(new FilterGreaterThanEvent(collectionManager));
            addCommand(new PrintFieldDescendingEvent(collectionManager));
            addCommand(new Info(collectionManager, fileName));
        }};
        commandManager.addCommand(new ExecuteScript(commandManager));
        commandManager.addCommand(new Help(commandManager));
        
        new Runner(collectionManager, commandManager).run();
    
        //TODO: redo some exception catching
        //TODO: redo some exceptions
        //TODO: remove memory leaks
        //TODO: make code clean
        //TODO: test all
        //TODO: add javadoc
        //TODO: make uml

        //TODO: Double and double stuff
    }
}
