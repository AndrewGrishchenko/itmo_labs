package lab5.commands;

import java.util.ArrayList;

import lab5.managers.CollectionManager;
import lab5.models.Ticket;
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
        ArrayList<Ticket> tickets = collectionManager.toArray();
        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }
        console.println(message);
        return true;
    }
}
