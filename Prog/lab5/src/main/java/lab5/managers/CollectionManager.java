package lab5.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lab5.exceptions.InvalidDataException;
import lab5.models.Ticket;
import lab5.models.Tickets;
import lab5.utility.TicketsComparator;

public class CollectionManager {
    private HashMap<Integer, Ticket> collection = new HashMap<>();
    private List<Integer> sortSequence = new ArrayList<>();

    public void addTicket(Ticket ticket) {
        collection.put(ticket.getId(), ticket);
        sortSequence.add(ticket.getId());
        sort();
    }

    public void dumpData(String fileName) throws IOException {
        //TODO: check file existance
        FileInputStream fileInputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
        String xml = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        
        XmlMapper xmlMapper = new XmlMapper();
        // xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        try {
            Tickets map = xmlMapper.readValue(xml, Tickets.class);
            List<Ticket> ticketsList = map.getTickets();
            for (int i = 0; i < ticketsList.size(); i++) {
                collection.put(ticketsList.get(i).getId(), ticketsList.get(i));
                sortSequence.add(ticketsList.get(i).getId());
            }

            validateAll();
            sort();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCollection() {
        collection.clear();
        sortSequence.clear();
    }

    private void validateAll() throws InvalidDataException {
        for (Ticket ticket : collection.values()) {
            if (!ticket.validate()) {
                if (!ticket.getCoordinates().validate()) {
                    throw new InvalidDataException("Тикет с id=" + String.valueOf(ticket.getId()) + " имеет невалидные координаты!"); 
                }
                else if (!ticket.getEvent().validate()) {
                    throw new InvalidDataException("Тикет с id=" + String.valueOf(ticket.getId()) + " имеет невалидный ивент!");
                }
                else {
                    throw new InvalidDataException("Тикет с id=" + String.valueOf(ticket.getId()) + " имеет невалидные данные!");
                }
            }
        }
    }

    private void sort() {
        Comparator<Integer> ticketsComparator = new TicketsComparator(collection);
        Collections.sort(sortSequence, ticketsComparator);
    }

    public int size() {
        return collection.size();
    }

    public Ticket get(int key) {
        return collection.get(key);
    }

    public List<Integer> getKeys() {
        return sortSequence;
    }
}
