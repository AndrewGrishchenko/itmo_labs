package lab5.utility;

import java.util.Comparator;
import java.util.HashMap;

import lab5.models.Ticket;

public class TicketsComparator implements Comparator<Integer> {
    private HashMap<Integer, Ticket> collection;
    
    public TicketsComparator(HashMap<Integer, Ticket> collection) {
        this.collection = collection;
    }
    
    @Override
    public int compare(Integer o1, Integer o2) {
        return collection.get(o1).getPrice() - collection.get(o2).getPrice();
    }
}
