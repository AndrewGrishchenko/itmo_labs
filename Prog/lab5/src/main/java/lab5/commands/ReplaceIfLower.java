package lab5.commands;

import java.time.format.DateTimeParseException;

import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.utility.console.Console;

public class ReplaceIfLower extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public ReplaceIfLower(Console console, CollectionManager collectionManager) {
        super("replace_if_lower", "заменить значение по ключу, если новое значение меньше старого", "'replace_if_lower <key>'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            console.println(getUsage());
            return false;
        }

        try {
            int id = Integer.parseInt(args[1]);
            Ticket oldTicket = collectionManager.getTicketById(id);
            Ticket newTicket = new Ticket();
            newTicket.fillData(console);

            if (!newTicket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }

            if (oldTicket.compareTo(newTicket) > 0) {
                collectionManager.changeTicketById(oldTicket.getId(), newTicket);
                console.println("Тикет заменен!");
            }
            else {
                console.println("Новое значение больше старого; тикет не заменен!");
            }

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
        } catch (IdNotFoundException e) {
            console.printErr(e.getMessage());
        }
        
        return false;
    }
}
