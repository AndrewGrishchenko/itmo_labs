package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;

public class Update extends Command {
    private CollectionManager collectionManager;
    
    public Update(CollectionManager collectionManager) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", "'update <id>'");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(getUsage());
            return false;
        }

        try {
            int id = Integer.parseInt(args[1]);
            Ticket ticket = collectionManager.getTicketById(id);
            Ticket oldTicket = new Ticket(ticket);

            ticket.fillData();

            if (!ticket.validate()) {
                ticket.restoreData(oldTicket);
                throw new InvalidDataException("Данные не прошли валидацию; тикет не был обновлен");
            }
            collectionManager.changeTicketById(id, ticket);
            ConsoleAdapter.println("Тикет обновлен!");

            return true;
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (IdNotFoundException e) {
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

        return true;
    }
}
