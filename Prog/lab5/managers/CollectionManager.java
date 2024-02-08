package managers;

import java.util.HashMap;
import models.Ticket;

public class CollectionManager {
    private HashMap<Integer, Ticket> collection;

    public CollectionManager() {

    }

    public HashMap<Integer, Ticket> getCollection() {
        return collection;
    }

    public void addTicket(Ticket ticket) {
        collection.put(ticket.getId(), ticket);
    }
}
