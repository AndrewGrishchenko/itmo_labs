package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.models.ExitCode;

/**
 * Команда 'replace_if_lower'. Заменяет значение по ключу, если новое значение меньше старого
 */
public class ReplaceIfLower extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public ReplaceIfLower(CollectionManager collectionManager) {
        super("replace_if_lower", "заменить значение по ключу, если новое значение меньше старого", "'replace_if_lower <key>'");
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
            Ticket oldTicket = collectionManager.getTicketById(id);
            Ticket newTicket = new Ticket();
            newTicket.fillData();

            if (oldTicket.compareTo(newTicket) > 0) {
                collectionManager.changeTicketById(oldTicket.getId(), newTicket);
                ConsoleAdapter.println("Тикет заменен!");
            }
            else {
                ConsoleAdapter.println("Новое значение больше старого; тикет не заменен!");
            }

            return ExitCode.OK;
        } catch (IdNotFoundException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("введенные данные должны являться числом!");
        }
        
        return ExitCode.ERROR;
    }
}
