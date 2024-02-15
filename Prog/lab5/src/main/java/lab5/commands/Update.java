package lab5.commands;

import java.time.format.DateTimeParseException;

import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.utility.console.Console;

public class Update extends Command {
    private Console console;
    private CollectionManager collectionManager;
    
    public Update(Console console, CollectionManager collectionManager) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", "'update <id>'");
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
            Ticket ticket = collectionManager.getTicketById(id);
            Ticket oldTicket = new Ticket(ticket);

            ticket.fillData(console);

            if (!ticket.validate()) {
                ticket.restoreData(oldTicket);
                console.printErr("Данные не прошли валидацию; тикет не был обновлен");
                return false;
            }
            collectionManager.changeTicketById(id, ticket);
            console.println("Тикет обновлен!");

            return true;
        } catch (IdNotFoundException e) {
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

        return true;
    }
}
