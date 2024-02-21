package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.InvalidDataException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.models.ExitCode;

/**
 * Команда 'remove_lower'. Удаляет из коллекции все элементы, меньшие чем заданный
 */
public class RemoveLower extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный", "'remove_lower'");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public ExitCode run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            Ticket ticket = new Ticket();
            ticket.fillData();

            if (!ticket.validate()) {
                throw new InvalidDataException("Тикет имеет невалидные данные!");
            }

            collectionManager.removeLowerThanTicket(ticket);
            ConsoleAdapter.println("тикеты удалены!");
            return ExitCode.OK;
        } catch (InvalidDataException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        return ExitCode.ERROR;
    }
}
