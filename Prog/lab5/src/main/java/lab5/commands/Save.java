package lab5.commands;

import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;

public class Save extends Command {
    private CollectionManager collectionManager;
    private String fileName;

    public Save(CollectionManager collectionManager, String fileName) {
        super("save", "сохранить коллекцию в файл", "'save'");
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return false;
        }
        
        try {
            collectionManager.saveData(fileName);
            ConsoleAdapter.println("файл сохранен!");
            return true;
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("Файл не найден!");
        } catch (JsonProcessingException e) {
            ConsoleAdapter.printErr("внутренняя ошибка!");
        }
        
        return false;
    }
}
