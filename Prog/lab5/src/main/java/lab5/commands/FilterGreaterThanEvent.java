package lab5.commands;

import java.util.List;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.models.Ticket;
import lab5.models.ExitCode;

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
        super("filter_greater_than_event", "вывести элементы, значение поля event которых больше заданных", "'filter_greater_than_event'");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        Event event = new Event();
        event.fillData();

        List<Ticket> tickets = collectionManager.filterGreaterByEvent(event);
        String message = "";
        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }
        ConsoleAdapter.println(message);
        return ExitCode.OK;
    }
}
