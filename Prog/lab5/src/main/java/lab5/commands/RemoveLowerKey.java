package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.ExitCode;

/**
 * Команда 'remove_lower_key'. Удаляет из коллекции все элементы, ключ которых меньше, чем заданный
 */
public class RemoveLowerKey extends Command {
    private CollectionManager collectionManager;

    /**
     * Конструктор команды
     * @param collectionManager менеджер коллекции
     * @see CollectionManager
     */
    public RemoveLowerKey(CollectionManager collectionManager) {
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный", "'remove_lower_key <key>'");
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
            ConsoleAdapter.println(getUsage());
            return ExitCode.ERROR;
        }

        try {
            int id = Integer.parseInt(args[1]);
            collectionManager.removeLowerThanId(id);
            ConsoleAdapter.println("Тикеты удалены!");
            return ExitCode.OK;
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            ConsoleAdapter.printErr("Введенные данные неверны!");
        }

        return ExitCode.ERROR;
    }
}
