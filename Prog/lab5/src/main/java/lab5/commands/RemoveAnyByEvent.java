package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.Event;
import lab5.models.ExitCode;

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
        super("remove_any_by_event", "удалить из коллекции один элемент, значение поля event которого эквивалентно заданному", "'remove_any_by_event'");
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

        Event event = new Event();
        event.fillData();
        
        if (collectionManager.removeOneByEvent(event)) {
            ConsoleAdapter.println("Тикет с данным ивентом был удален!");
        }
        else {
            ConsoleAdapter.println("Данный ивент нигде не используется; ни один тикет не удален");
        }
        return ExitCode.OK;
    }
}
