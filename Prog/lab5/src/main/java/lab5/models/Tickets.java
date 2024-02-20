package lab5.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * Класс необходимый для (де)сериализации xml
 */
public class Tickets {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Ticket> ticket = new ArrayList<>();

    /**
     * Возвращает список объектов типа {@link Ticket}
     * @return список объектов типа {@link Ticket}
     */
    public List<Ticket> getTicket() {
        return ticket;
    }

    /**
     * Устанавливает список объектов типа {@link Ticket}
     * @param ticket список объектов типа {@link Ticket}
     */
    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    /**
     * Добавляет объект типа {@link Ticket}
     * @param ticket объект типа {@link Ticket}
     */
    public void addTicket(Ticket ticket) {
        this.ticket.add(ticket);
    }
}
