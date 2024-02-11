package lab5.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Tickets {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Ticket> ticket = new ArrayList<>();

    public List<Ticket> getTickets() {
        return ticket;
    }
}
