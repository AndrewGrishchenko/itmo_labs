package lab5.commands;

import java.util.ArrayList;

import lab5.adapters.ConsoleAdapter;
import lab5.managers.CollectionManager;
import lab5.models.Ticket;

public class Show extends Command {
    private CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", "'show'");
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean run(String[] args) {
        if (args.length != 1) {
            ConsoleAdapter.println(getUsage());
            return false;
        }
        
        String message = "";
        ArrayList<Ticket> tickets = collectionManager.toArray();
        for (Ticket ticket : tickets) {
            message += ticket.toString();
        }
        ConsoleAdapter.println(message);
        return true;
    }
}
