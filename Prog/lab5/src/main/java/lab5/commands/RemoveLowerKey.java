package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.utility.Runner.ExitCode;

public class RemoveLowerKey extends Command {
    private CollectionManager collectionManager;

    public RemoveLowerKey(CollectionManager collectionManager) {
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный", "'remove_lower_key <key>'");
        this.collectionManager = collectionManager;
    }

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
