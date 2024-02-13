package lab5.commands;

import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class Clear extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", "'clear'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        collectionManager.clearCollection();
        console.println("Коллекция очищена!");
        return true;
    }
}
