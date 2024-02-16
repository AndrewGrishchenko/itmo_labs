package lab5.commands;

import java.util.List;

import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.utility.console.Console;

public class PrintFieldDescendingEvent extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public PrintFieldDescendingEvent(Console console, CollectionManager collectionManager) {
        super("print_field_descending_event", "вывести значения поля event всех элементов в порядке убывания", "'print_field_descending_event'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            console.println(getUsage());
            return false;
        }

        List<Event> events = collectionManager.sortedDescendingEvents();
        String message = "";
        for (Event event : events) {
            message += event.toString();
        }
        console.println(message);

        return true;
    }
}
