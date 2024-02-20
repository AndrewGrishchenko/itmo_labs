package lab5.commands;

import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.InvalidDataException;
import lab5.exceptions.TooManyArgumentsException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.utility.Runner.ExitCode;

public class ReplaceIfLower extends Command {
    private CollectionManager collectionManager;

    public ReplaceIfLower(CollectionManager collectionManager) {
        super("replace_if_lower", "заменить значение по ключу, если новое значение меньше старого", "'replace_if_lower <key>'");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExitCode run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            int id = Integer.parseInt(args[1]);
            Ticket oldTicket = collectionManager.getTicketById(id);
            Ticket newTicket = new Ticket();
            newTicket.fillData();

            if (!newTicket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }

            if (oldTicket.compareTo(newTicket) > 0) {
                collectionManager.changeTicketById(oldTicket.getId(), newTicket);
                ConsoleAdapter.println("Тикет заменен!");
            }
            else {
                ConsoleAdapter.println("Новое значение больше старого; тикет не заменен!");
            }

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
        } catch (IdNotFoundException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (ZoneRulesException e) {
            ConsoleAdapter.printErr("ошибка формата зоны!");
        }
        
        return ExitCode.ERROR;
    }
}
