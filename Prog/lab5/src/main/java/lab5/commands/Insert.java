package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotUniqueException;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.models.ExitCode;

/**
 * Команда 'insert'. Добавляет ноый элемент с заданным ключом
 */
public class Insert extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public Insert(CollectionManager collectionManager) {
        super("insert", "добавить новый элемент с заданным ключом", "'insert <key>'");
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
            ConsoleAdapter.println(this.getUsage());
            return ExitCode.ERROR;
        }
        
        try {
            int id = Integer.parseInt(args[1]);
            if (collectionManager.hasId(id)) throw new IdNotUniqueException("Тикет с id=" + args[1] + " уже существует!"); 

            Ticket ticket = new Ticket(id);
            ticket.fillData();

            ConsoleAdapter.println("Тикет был создан!");
            collectionManager.addTicket(ticket);

            return ExitCode.OK;
        } catch (IdNotUniqueException e) {
            ConsoleAdapter.printErr(e.getMessage());
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("данные должны являться числом!");
        }

        return ExitCode.ERROR;
    }
}
