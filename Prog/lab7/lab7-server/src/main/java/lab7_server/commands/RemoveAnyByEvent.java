package lab7_server.commands;

import lab7_server.managers.CollectionManager;
import lab7_server.models.ExitCode;
import lab7_core.models.Event;

/**
 * Команда 'remove_any_by_event'. Удаляет из коллекции один элемент, значение поля event которого эквивалентно заданному
 */
public class RemoveAnyByEvent extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveAnyByEvent(CollectionManager collectionManager) {
        super("remove_any_by_event", "удалить из коллекции один элемент, значение поля event которого эквивалентно заданному", "'remove_any_by_event'", "event");
        this.collectionManager = collectionManager;
    }

    /**
     * Запуск команды
     * @return код завершения команды
     * @see ExitCode
     */
    @Override
    public String invoke() {
        Event event = (Event) getObj();
        
        if (collectionManager.removeOneByEvent(event)) {
            return "Тикет с данным ивентом был удален!";
        }
        else {
            return "Данный ивент нигде не используется; ни один тикет не удален";
        }
    }

    @Override
    public String isValid() {
        if (getArgs().length != 1) return getUsage();
        return null;
    }
}
