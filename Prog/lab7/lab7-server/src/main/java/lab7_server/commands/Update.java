package lab7_server.commands;

import lab7_server.exceptions.IdNotFoundException;
import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Ticket;

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
        super("update", "обновить значение элемента коллекции, id которого равен заданному", "'update <id>'", "ticket", 2);
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
        if (args.length != 2) {
            return getUsage();
        }

        try {
            int id = Integer.parseInt(args[1]);
            
            Ticket newTicket = (Ticket) getObj();
            newTicket.setId(id);

            collectionManager.changeTicketById(id, newTicket);
            return "Тикет обновлен!";
        } catch (IdNotFoundException e) {
            return e.getMessage();
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
    }

    @Override
    public String isValid() {
        //TODO: remove isValid, check ids and other in command, and create commandBuilder
        if (!this.getMeta().testArgC(getArgs().length)) return getUsage();
        try {
            if (!collectionManager.hasId(Integer.parseInt(getArgs()[1]))) return "Тикет с данным id не найден!";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
