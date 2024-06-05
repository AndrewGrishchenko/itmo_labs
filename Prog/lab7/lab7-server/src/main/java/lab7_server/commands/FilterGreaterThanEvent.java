package lab7_server.commands;

import java.util.List;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Event;
import lab7_core.models.Ticket;

/**
 * Команда 'filter_greater_than_event'. Выводит элементы, значение поля event которых больше заданных
 */
public class FilterGreaterThanEvent extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public FilterGreaterThanEvent(CollectionManager collectionManager) {
        super("filter_greater_than_event", "вывести элементы, значение поля event которых больше заданных", "'filter_greater_than_event'", "event", 1);
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        Event event = (Event) getObj();

        List<Ticket> tickets = collectionManager.filterGreaterByEvent(event);
        String message = "";
        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }
        
        return message;
    }
}
