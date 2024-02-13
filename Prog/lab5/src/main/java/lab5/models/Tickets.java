package lab5.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Tickets {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Ticket> ticket = new ArrayList<>();

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public void addTicket(Ticket ticket) {
        this.ticket.add(ticket);
    }
}
