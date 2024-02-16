package lab5.commands;

import java.time.format.DateTimeParseException;

import lab5.exceptions.IdNotUniqueException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
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
            if (collectionManager.hasId(id)) throw new IdNotUniqueException("Тикет с id=" + args[1] + " уже существует!"); 

            Ticket ticket = new Ticket(id);
            ticket.fillData(console);

            if (!ticket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }
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
        } catch (DateTimeParseException e) {
            console.printErr("ошибка формата даты!");
        }

        return false;
    }
}
