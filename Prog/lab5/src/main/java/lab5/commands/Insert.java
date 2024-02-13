package lab5.commands;

import lab5.adapters.ScannerAdapter;
import lab5.exceptions.IdNotUniqueException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Coordinates;
import lab5.models.Event;
import lab5.models.Ticket;
import lab5.models.TicketType;
import lab5.utility.console.Console;

public class Insert extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public Insert(Console console, CollectionManager collectionManager) {
        super("insert", "добавить новый элемент с заданным ключом", "'insert <key>'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            console.println(this.getUsage());
            return false;
        }
        
        try {
            int id = Integer.parseInt(args[1]);
            if (collectionManager.hasId(id)) throw new IdNotUniqueException("Тикет с данным id=" + args[1] + " уже существует!"); 

            ScannerAdapter.setMultiMode();
            Ticket ticket = new Ticket(id);
            console.print("Введите name: ");
            ticket.setName(ScannerAdapter.getString());

            Coordinates coordinates = new Coordinates();
            console.print("Введите x: ");
            coordinates.setX(ScannerAdapter.getDouble());

            console.print("Введите y: ");
            coordinates.setY(ScannerAdapter.getDouble());
            ticket.setCoordinates(coordinates);

            console.print("Введите price: ");
            ticket.setPrice(ScannerAdapter.getInt());

            console.print("Введите type: ");
            ticket.setType(TicketType.valueOf(ScannerAdapter.getString()));

            Event event = new Event();
            console.print("Введите name: ");
            event.setName(ScannerAdapter.getString());

            //TODO: check date
            console.print("Введите date: ");
            event.setDate(ScannerAdapter.getString());

            console.print("Введите ticketsCount: ");
            event.setTicketsCount(ScannerAdapter.getLong());

            console.print("Введите description: ");
            event.setDescription(ScannerAdapter.getString());
            ticket.setEvent(event);

            ticket.validate();
            console.println("Тикет был создан!");
            collectionManager.addTicket(ticket);
            
            return true;
        } catch (NumberFormatException e) {
            console.printErr("данные должны являться числом!");
        } catch (IdNotUniqueException e) {
            console.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            console.printErr(e.getMessage());
        } catch (InvalidDataException e) {
            console.printErr(e.getMessage());
        } catch (IllegalArgumentException e) {
            console.printErr("Введенные данные неверны!");
        }

        return false;
    }
}
