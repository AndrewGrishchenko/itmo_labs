package lab7_server.commands;

import lab7_core.models.Ticket;
import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;

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
        super("clear", "очистить коллекцию", "'clear'", true, 1);
        
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run () {
        for (Ticket ticket : collectionManager.toArray()) {
            collectionManager.removeTicketByUser(ticket.getId(), getAuthManager().getUserId());
        }
        return "Ваши элементы коллекции очищены!";
    }
}
