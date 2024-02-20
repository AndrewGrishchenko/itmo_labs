package lab5.commands;

import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.utility.Runner.ExitCode;

public class Save extends Command {
    private CollectionManager collectionManager;
    private String fileName;

    public Save(CollectionManager collectionManager, String fileName) {
        super("save", "сохранить коллекцию в файл", "'save'");
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }
        
        try {
            collectionManager.saveData(fileName);
            ConsoleAdapter.println("Файл сохранен!");
            return ExitCode.OK;
        } catch (FileNotFoundException e) {
            ConsoleAdapter.printErr("файл не найден!");
        } catch (JsonProcessingException e) {
            ConsoleAdapter.printErr("внутренняя ошибка!");
        }
        
        return ExitCode.ERROR;
    }
}
