package lab5.commands;

import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.ExitCode;

/**
 * Команда 'save'. Сохраняет коллекцию в файл
 */
public class Save extends Command {
    private CollectionManager collectionManager;
    private String fileName;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @param fileName имя файла
     * @see CollectionManager
     */
    public Save(CollectionManager collectionManager, String fileName) {
        super("save", "сохранить коллекцию в файл", "'save'");
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
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
