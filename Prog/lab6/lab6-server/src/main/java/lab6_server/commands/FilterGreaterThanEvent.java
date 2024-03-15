package lab6_server.commands;

import java.util.List;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;
import lab6_core.models.Event;
import lab6_core.models.Ticket;

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
        super("filter_greater_than_event", "вывести элементы, значение поля event которых больше заданных", "'filter_greater_than_event'", "event");
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

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
