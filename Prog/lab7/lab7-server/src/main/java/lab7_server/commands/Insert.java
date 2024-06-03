package lab7_server.commands;

import lab7_server.exceptions.IdNotUniqueException;
import lab7_server.managers.CollectionManager;
import lab7_core.models.Ticket;
import lab7_server.models.ExitCode;

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
        super("insert", "добавить новый элемент с заданным ключом", "'insert <key>'", "ticket");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String run() {
        String[] args = getArgs();
        
        try {
            int id = Integer.parseInt(args[1]);
            if (collectionManager.hasId(id)) throw new IdNotUniqueException("Тикет с id=" + args[1] + " уже существует!"); 

            Ticket ticket = (Ticket) getObj();
            ticket.setId(id);

            
            collectionManager.addTicket(ticket);
            return "Тикет был создан!";
        } catch (IdNotUniqueException e) {
            return e.getMessage();
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 2) return getUsage();
        try {
            if (collectionManager.hasId(Integer.parseInt(getArgs()[1]))) return "Тикет с id=" + getArgs()[1] + " уже существует!";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
