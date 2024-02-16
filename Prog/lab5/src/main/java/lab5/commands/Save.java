package lab5.commands;

import java.io.FileNotFoundException;
import java.io.IOException;

import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class Save extends Command {
    private Console console;
    private CollectionManager collectionManager;
    private String fileName;

    public Save(Console console, CollectionManager collectionManager, String fileName) {
        super("save", "сохранить коллекцию в файл", "'save'");
        this.console = console;
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    @Override
    public boolean run(String[] args) {
        try {
            collectionManager.saveData(fileName);
            return true;
        } catch (FileNotFoundException e) {
            console.printErr("Файл не найден!");
        } catch (IOException e) {
            e.printStackTrace();
            // console.printErr("Ошибка ввода/вывода!");
        }
        
        return false;
    }
}
