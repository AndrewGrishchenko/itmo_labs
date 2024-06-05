package lab7_server.commands;

import java.util.List;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Event;

/**
 * Команда 'print_field_descending_event'. Выводит значения поля event всех элементов в порядке убывания
 */
public class PrintFieldDescendingEvent extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public PrintFieldDescendingEvent(CollectionManager collectionManager) {
        super("print_field_descending_event", "вывести значения поля event всех элементов в порядке убывания", "'print_field_descending_event'", true, 1);
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        List<Event> events = collectionManager.sortedDescendingEvents();
        String message = "";
        for (Event event : events) {
            message += event.toString();
        }
        
        return message;
    }
}
