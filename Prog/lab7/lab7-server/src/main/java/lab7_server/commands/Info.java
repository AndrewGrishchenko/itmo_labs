package lab7_server.commands;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;

/**
 * Команда 'info'. Выводит информацию о коллекции
 */
public class Info extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @param fileName имя файла
     * @see CollectionManager
     */
    public Info(CollectionManager collectionManager) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)", "'info'", true, 1);
        this.collectionManager = collectionManager;
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
        + "\n  Количество элементов: " + collectionManager.getSize() + "\n";

        return message;
    }
}
