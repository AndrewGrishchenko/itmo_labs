package lab5.commands;

import java.util.List;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.utility.Runner.ExitCode;

public class PrintFieldDescendingEvent extends Command {
    private CollectionManager collectionManager;

    public PrintFieldDescendingEvent(CollectionManager collectionManager) {
        super("print_field_descending_event", "вывести значения поля event всех элементов в порядке убывания", "'print_field_descending_event'");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        List<Event> events = collectionManager.sortedDescendingEvents();
        String message = "";
        for (Event event : events) {
            message += event.toString();
        }
        ConsoleAdapter.println(message);

        return ExitCode.OK;
    }
}
