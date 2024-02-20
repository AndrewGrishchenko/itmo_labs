package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.managers.CollectionManager;
import lab5.models.ExitCode;

/**
 * Команда 'remove_key'. Удаляет элемент коллекции по его ключу
 */
public class RemoveKey extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveKey(CollectionManager collectionManager) {
        super("remove_key", "удалить элемент из коллекции по его ключу", "'remove_key <key>'");
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
            collectionManager.removeTicketById(id);
            ConsoleAdapter.println("Тикет с id=" + String.valueOf(id) + " удален!");
            return ExitCode.OK;
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("id должен являться числом!");
        } catch (IdNotFoundException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        return ExitCode.ERROR;
    }
}
