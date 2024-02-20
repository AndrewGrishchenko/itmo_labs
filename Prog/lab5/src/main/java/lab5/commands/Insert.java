package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotUniqueException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;

public class Insert extends Command {
    private CollectionManager collectionManager;

    public Insert(CollectionManager collectionManager) {
        super("insert", "добавить новый элемент с заданным ключом", "'insert <key>'");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(this.getUsage());
            return false;
        }
        
        try {
            int id = Integer.parseInt(args[1]);
            if (collectionManager.hasId(id)) throw new IdNotUniqueException("Тикет с id=" + args[1] + " уже существует!"); 

            Ticket ticket = new Ticket(id);
            ticket.fillData();

            if (!ticket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }
            ConsoleAdapter.println("Тикет был создан!");
            collectionManager.addTicket(ticket);

            return true;
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("данные должны являться числом!");
        } catch (IdNotUniqueException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (TooManyArgumentsException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (IllegalArgumentException e) {
            ConsoleAdapter.printErr("Введенные данные неверны!");
        } catch (DateTimeParseException e) {
            ConsoleAdapter.printErr("ошибка формата даты!");
        } catch (ZoneRulesException e) {
            ConsoleAdapter.printErr("ошибка формата зоны!");
        }

        return false;
    }
}
