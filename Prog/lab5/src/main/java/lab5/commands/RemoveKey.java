package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.exceptions.IdNotFoundException;
import lab5.managers.CollectionManager;

public class RemoveKey extends Command {
    private CollectionManager collectionManager;

    public RemoveKey(CollectionManager collectionManager) {
        super("remove_key", "удалить элемент из коллекции по его ключу", "'remove_key <key>'");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 2) {
            ConsoleAdapter.println(this.getUsage());
            return false;
        }

        try {
            int id = Integer.parseInt(args[1]);
            collectionManager.removeTicketById(id);
            ConsoleAdapter.println("Тикет с id=" + String.valueOf(id) + " удален!");
            return true;
        } catch (NumberFormatException e) {
            ConsoleAdapter.printErr("id должен являться числом!");
        } catch (IdNotFoundException e) {
            ConsoleAdapter.printErr(e.getMessage());
        }

        return false;
    }
}
