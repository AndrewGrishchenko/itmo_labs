package lab5.commands;

import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class RemoveLowerKey extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public RemoveLowerKey(Console console, CollectionManager collectionManager) {
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный", "'remove_lower_key <key>'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            console.println(getUsage());
            return false;
        }

        try {
            int id = Integer.parseInt(args[1]);
            collectionManager.removeLowerThanId(id);
            console.println("Тикеты удалены!");
            return true;
        } catch (NumberFormatException e) {
            console.printErr("данные должны являться числом!");
        } catch (IllegalArgumentException e) {
            console.printErr("Введенные данные неверны!");
        }

        return false;
    }
}
