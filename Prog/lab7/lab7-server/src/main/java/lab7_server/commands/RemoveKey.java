package lab7_server.commands;

import lab7_server.exceptions.IdNotFoundException;
import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;

/**
 * Команда 'remove_key'. Удаляет элемент коллекции по его ключу
 */
public class RemoveKey extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveKey(CollectionManager collectionManager) {
        super("remove_key", "удалить элемент из коллекции по его ключу", "'remove_key <key>'");
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
            collectionManager.removeTicketById(id);
            return "Тикет с id=" + String.valueOf(id) + " удален!";
        } catch (NumberFormatException e) {
            return "id должен являться числом!";
        } catch (IdNotFoundException e) {
            return e.getMessage();
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 2) return getUsage();
        try {
            if (!collectionManager.hasId(Integer.parseInt(getArgs()[1]))) return "Тикет с данным id не найден";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
