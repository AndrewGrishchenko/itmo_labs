package lab5.commands;

import java.util.List;

import lab5.managers.CollectionManager;
import lab5.utility.console.Console;

public class Show extends Command {
    private Console console;
    private CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", "'show'");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        String message = "";
        List<Integer> keySet = collectionManager.getKeys();
        for (Integer key : keySet) {
            message += collectionManager.get(key).toString();
        }
        console.println(message);
        return true;
    }
}
