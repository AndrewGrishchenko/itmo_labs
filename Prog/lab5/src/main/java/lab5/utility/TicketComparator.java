package lab5.utility;

import java.util.Comparator;
import java.util.HashMap;

import lab5.models.Ticket;

/**
 * Компаратор для сортировки id элементов коллекции по соответствующим значениям
 */
public class TicketComparator implements Comparator<Integer> {
    private HashMap<Integer, Ticket> collection;
    
    /**
     * Конструктор компаратора
     * @param collection коллекция
     */
    public TicketComparator(HashMap<Integer, Ticket> collection) {
        this.collection = collection;
    }
    
    @Override
    public int compare(Integer o1, Integer o2) {
        return collection.get(o1).getPrice() - collection.get(o2).getPrice();
    }
}
