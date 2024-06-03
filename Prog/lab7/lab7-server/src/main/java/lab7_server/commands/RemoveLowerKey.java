package lab7_server.commands;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;

/**
 * Команда 'remove_lower_key'. Удаляет из коллекции все элементы, ключ которых меньше, чем заданный
 */
public class RemoveLowerKey extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveLowerKey(CollectionManager collectionManager) {
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный", "'remove_lower_key <key>'");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        String[] args = getArgs();

        try {
            int id = Integer.parseInt(args[1]);
            collectionManager.removeLowerThanId(id);
            return "Тикеты удалены!";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        } catch (IllegalArgumentException e) {
            return "Введенные данные неверны!";
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 2) return getUsage();
        try {
            Integer.parseInt(getArgs()[1]);
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
