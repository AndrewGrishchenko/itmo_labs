package lab6_server.commands;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;
import lab6_core.models.Ticket;

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
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный", "'remove_lower'", "ticket");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String invoke() {
        Ticket ticket = (Ticket) getObj();

        collectionManager.removeLowerThanTicket(ticket);
        return "Тикеты удалены!";
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
