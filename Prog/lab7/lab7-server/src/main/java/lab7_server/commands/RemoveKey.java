package lab7_server.commands;

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
        super("remove_key", "удалить элемент из коллекции по его ключу", "'remove_key <key>'", true, 2);
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
            if(collectionManager.removeTicketByUser(id, getAuthManager().getUserId()))
                return "Тикет с id=" + String.valueOf(id) + " удален!";
            else
                return "Данный тикет не принадлежит вам";
        } catch (NumberFormatException e) {
            return "id должен являться числом!";
        }
    }

    @Override
    public String isValid() {
        if (!this.getMeta().testArgC(getArgs().length)) return getUsage();
        try {
            if (!collectionManager.hasId(Integer.parseInt(getArgs()[1]))) return "Тикет с данным id не найден";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
