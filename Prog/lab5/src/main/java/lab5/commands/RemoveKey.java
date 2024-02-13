package lab5.commands;

import lab5.exceptions.IdNotFoundException;
import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class RemoveKey extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public RemoveKey(Console console, CollectionManager collectionManager) {
        super("remove_key", "удалить элемент из коллекции по его ключу", "'remove_key <key>'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            console.println(this.getUsage());
            return false;
        }

        try {
            int id = Integer.parseInt(args[1]);
            collectionManager.removeTicketById(id);
            console.println("Тикет с id=" + String.valueOf(id) + " удален!");
            return true;
        } catch (NumberFormatException e) {
            console.printErr("id должен являться числом!");
        } catch (IdNotFoundException e) {
            console.printErr(e.getMessage());
        }

        return false;
    }
}
