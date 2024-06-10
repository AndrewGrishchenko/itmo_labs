package lab7_server.commands;

import lab7_core.models.Ticket;
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
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный", "'remove_lower_key <key>'", true, 2);
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

        int id = Integer.parseInt(args[1]);
        int count = 0;
        for (Ticket ticket : collectionManager.toArray()) {
            if (ticket.getId() < id) {
                if(collectionManager.removeTicketByUser(ticket.getId(), getAuthManager().getUserId())) count++;
            }
        }
        return "Удалено " + String.valueOf(count) + " тикетов!";
    }

    @Override
    public String isValid() {
        if (!this.getMeta().testArgC(getArgs().length)) return getUsage();
        try {
            Integer.parseInt(getArgs()[1]);
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
