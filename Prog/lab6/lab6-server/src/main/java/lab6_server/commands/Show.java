package lab6_server.commands;

import java.util.ArrayList;

import lab6_server.managers.CollectionManager;
import lab6_server.models.ExitCode;
import lab6_core.models.Ticket;

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
    public String run() {
        
        String message = "";
        ArrayList<Ticket> tickets = collectionManager.toArray();
        
        if (tickets.size() == 0) {
            return "Коллекция пуста!";
        }

        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }

        return message;
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
