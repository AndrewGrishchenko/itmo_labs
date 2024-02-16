package lab5.utility;

import java.util.Comparator;

import lab5.models.Event;

public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        return (int) (o1.getTicketsCount() - o2.getTicketsCount());
    }
}
