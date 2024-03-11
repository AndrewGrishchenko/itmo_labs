package lab5.commands;

import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;
import lab5.models.ExitCode;

/**
 * Команда 'show'. Выводит все элементы коллекции
 */
public class Show extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public Show(CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", "'show'");
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
        
        String message = "";
        ArrayList<Ticket> tickets = collectionManager.toArray();
        
        if (tickets.size() == 0) {
            ConsoleAdapter.println("Коллекция пуста!");
            return ExitCode.OK;
        }

        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }
        ConsoleAdapter.println(message);
        return ExitCode.OK;
    }
}
