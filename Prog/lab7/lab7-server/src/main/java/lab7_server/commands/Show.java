package lab7_server.commands;

import java.util.ArrayList;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Ticket;

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
    public String invoke() {
        
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
