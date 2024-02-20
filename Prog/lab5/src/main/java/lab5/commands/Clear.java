package lab5.commands;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;

public class Clear extends Command {
    private CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", "'clear'");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return false;
        }
        
        collectionManager.clearCollection();
        ConsoleAdapter.println("Коллекция очищена!");
        return true;
    }
}
