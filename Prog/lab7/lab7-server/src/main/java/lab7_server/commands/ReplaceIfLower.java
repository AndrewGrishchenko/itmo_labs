package lab7_server.commands;

import lab7_server.exceptions.IdNotFoundException;
import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Ticket;

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
        super("replace_if_lower", "заменить значение по ключу, если новое значение меньше старого", "'replace_if_lower <key>'", "ticket", 2);
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
            Ticket oldTicket = collectionManager.getTicketById(id);
            Ticket newTicket = (Ticket) getObj();

            if (oldTicket.compareTo(newTicket) > 0) {
                if (collectionManager.replaceTicket(oldTicket, newTicket, getAuthManager().getUserId())) return "Тикет заменен!";
                return "Тикет не принадлежит вам!";
            }
            
            return "Новое значение больше старого; тикет не заменен!";
        } catch (IdNotFoundException e) {
            return e.getMessage();
        }
    }

    @Override
    public String isValid() {
        if (!this.getMeta().testArgC(getArgs().length)) return getUsage();
        try {
            if (!collectionManager.hasId(Integer.parseInt(getArgs()[1]))) return "Тикет с данным id не найден";
        } catch (NumberFormatException e) {
            return "Данные должны являться числом!";
        }
        return null;
    }
}
