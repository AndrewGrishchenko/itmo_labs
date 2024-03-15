package lab6_server.commands;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;

/**
 * Команда 'info'. Выводит информацию о коллекции
 */
public class Info extends Command {
    private CollectionManager collectionManager;
    private String fileName;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @param fileName имя файла
     * @see CollectionManager
     */
    public Info(CollectionManager collectionManager, String fileName) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)", "'info'");
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
        String message = "Информация о коллекции:\n  Тип: " + collectionManager.getType()
        + "\n  Дата инициализации: " + collectionManager.getInitTime()
        + "\n  Дата последнего изменения: " + collectionManager.getLastUpdateTime()
        + "\n  Количество элементов: " + collectionManager.getSize()
        + "\n  Название xml файла: " + fileName + "\n";

        return message;
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
