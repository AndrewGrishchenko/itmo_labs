package lab5.commands;

import java.time.format.DateTimeParseException;

import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.utility.console.Console;

public class RemoveLower extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public RemoveLower(Console console, CollectionManager collectionManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный", "'remove_lower'");
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
            Ticket ticket = new Ticket();
            ticket.fillData(console);

            if (!ticket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }

            collectionManager.removeLowerThanTicket(ticket);
            console.println("тикеты удалены!");
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
