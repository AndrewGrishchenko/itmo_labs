package lab5.commands;

import java.time.format.DateTimeParseException;
import java.util.List;

import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.models.Ticket;
import lab5.utility.console.Console;

public class FilterGreaterThanEvent extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public FilterGreaterThanEvent(Console console, CollectionManager collectionManager) {
        super("filter_greater_than_event", "вывести элементы, значение поля event которых больше заданных", "'filter_greater_than_event'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            console.println(getUsage());
            return false;
        }

        try {
            Event event = new Event();
            event.fillData(console);

            if (!event.validate()) {
                throw new InvalidDataException("Ивент имеет невалидные данные!");
            }

            List<Ticket> tickets = collectionManager.filterGreaterByEvent(event);
            String message = "";
            for (Ticket ticket : tickets) {
                message += ticket.toString();
            }
            console.println(message);
            return true;
        } catch (InvalidDataException e) {
            console.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            console.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            console.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            console.printErr("Введенные данные неверны!");
        } catch (DateTimeParseException e) {
            console.printErr("ошибка формата даты!");
        }

        return false;
    }
}
