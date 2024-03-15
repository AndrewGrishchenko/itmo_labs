package lab6_server.commands;

import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;

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
    public String run() {
        try {
            collectionManager.saveData(fileName);
            return "Файл сохранен!";
        } catch (FileNotFoundException e) {
            return "Файл не найден!";
        } catch (JsonProcessingException e) {
            return "Внутренняя ошибка!";
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
