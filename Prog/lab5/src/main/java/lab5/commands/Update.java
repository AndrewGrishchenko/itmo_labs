package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.InvalidDataException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.models.ExitCode;

/**
 * Команда 'update'. Обновляет значение элемента коллекции по ключу
 */
public class Update extends Command {
    private CollectionManager collectionManager;
    
    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public Update(CollectionManager collectionManager) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", "'update <id>'");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public ExitCode run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
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

            return ExitCode.OK;
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (IdNotFoundException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("введенные данные должны являться числом!");
        }

        return ExitCode.ERROR;
    }
}
