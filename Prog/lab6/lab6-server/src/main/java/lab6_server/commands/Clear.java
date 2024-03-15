package lab6_server.commands;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;

/**
 * Команда 'clear'. Очищает коллекцию
 */
public class Clear extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", "'clear'");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        collectionManager.clearCollection();
        return"Коллекция очищена!";
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
