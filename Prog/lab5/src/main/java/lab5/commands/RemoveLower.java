package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.utility.Runner.ExitCode;

public class RemoveLower extends Command {
    private CollectionManager collectionManager;

    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный", "'remove_lower'");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            Ticket ticket = new Ticket();
            ticket.fillData();

            if (!ticket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }

            collectionManager.removeLowerThanTicket(ticket);
            ConsoleAdapter.println("тикеты удалены!");
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
