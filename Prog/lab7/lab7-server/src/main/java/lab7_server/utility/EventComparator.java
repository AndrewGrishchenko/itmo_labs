package lab7_server.utility;

import java.util.Comparator;

import lab7_core.models.Event;

/**
 * Компаратор для объектов класса {@link Event}
 */
public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        if (o1.getTicketsCount() == null && o2.getTicketsCount() == null) return 0;
        if (o1.getTicketsCount() == null) return -1;
        if (o2.getTicketsCount() == null) return 1;
        return (int) (o1.getTicketsCount() - o2.getTicketsCount());
    }
}
