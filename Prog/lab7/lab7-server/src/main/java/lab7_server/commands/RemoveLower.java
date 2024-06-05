package lab7_server.commands;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Ticket;

/**
 * Команда 'remove_lower'. Удаляет из коллекции все элементы, меньшие чем заданный
 */
public class RemoveLower extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный", "'remove_lower'", "ticket", 1);
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        Ticket ticket = (Ticket) getObj();

        collectionManager.removeLowerThanTicket(ticket);
        return "Тикеты удалены!";
    }
}
