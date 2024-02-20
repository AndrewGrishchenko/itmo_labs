package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.List;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.models.Ticket;
import lab5.utility.Runner.ExitCode;

public class FilterGreaterThanEvent extends Command {
    private CollectionManager collectionManager;

    public FilterGreaterThanEvent(CollectionManager collectionManager) {
        super("filter_greater_than_event", "вывести элементы, значение поля event которых больше заданных", "'filter_greater_than_event'");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            Event event = new Event();
            event.fillData();

            if (!event.validate()) {
                throw new InvalidDataException("Ивент имеет невалидные данные!");
            }

            List<Ticket> tickets = collectionManager.filterGreaterByEvent(event);
            String message = "";
            for (Ticket ticket : tickets) {
                message += ticket.toString();
            }
            ConsoleAdapter.println(message);
            return ExitCode.OK;
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            ConsoleAdapter.printErr("Введенные данные неверны!");
        } catch (DateTimeParseException e) {
            ConsoleAdapter.printErr("ошибка формата даты!");
        } catch (ZoneRulesException e) {
            ConsoleAdapter.printErr("ошибка формата зоны!");
        }

        return ExitCode.ERROR;
    }
}
