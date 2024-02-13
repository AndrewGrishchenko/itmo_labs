package lab5.managers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lab5.exceptions.IdNotFoundException;
import lab5.exceptions.InvalidDataException;
import lab5.models.Ticket;
import lab5.models.Tickets;
import lab5.utility.TicketsComparator;

public class CollectionManager {
    private HashMap<Integer, Ticket> collection = new HashMap<>();
    private List<Integer> sortSequence = new ArrayList<>();

    public void addTicket(Ticket ticket) throws InvalidDataException {
        collection.put(ticket.getId(), ticket);
        sortSequence.add(ticket.getId());
        validateAll();
        sort();        
    }

    public void dumpData(String fileName) throws InvalidDataException, IdNotFoundException, FileNotFoundException, IOException {
        //TODO: check file existance
        FileInputStream fileInputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
        String xml = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        
        XmlMapper xmlMapper = new XmlMapper();

        Tickets map = xmlMapper.readValue(xml, Tickets.class);
        List<Ticket> ticketsList = map.getTicket();
        for (int i = 0; i < ticketsList.size(); i++) {
            collection.put(ticketsList.get(i).getId(), ticketsList.get(i));
            sortSequence.add(ticketsList.get(i).getId());
        }

        validateAll();
        sort();
    }

    private Tickets toTickets() {
        Tickets tickets = new Tickets();
        for (int i = 0; i < sortSequence.size(); i++) {
            tickets.addTicket(collection.get(sortSequence.get(i)));
        }

        return tickets;
    }

    public void saveData(String fileName) throws JsonProcessingException, FileNotFoundException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Tickets tickets = toTickets();
        String xml = xmlMapper.writeValueAsString(tickets);

        PrintWriter printWriter = new PrintWriter(fileName);
        printWriter.println(xml);
        printWriter.close();
    }

    public void clearCollection() {
        collection.clear();
        sortSequence.clear();
    }

    public void removeTicketById(int id) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id) + " не существует!");
        }
        collection.remove(id);
        for (int i = 0; i < sortSequence.size(); i++) {
            if (sortSequence.get(i) == id) {
                sortSequence.remove(i);
                return;
            }
        }
    }

    private void validateAll() throws InvalidDataException {
        for (Ticket ticket : collection.values()) { 
            if (!ticket.validate()) {
                try {
                    removeTicketById(ticket.getId());
                } catch (Exception e) {}
                
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

    public boolean hasId(int key) {
        return collection.containsKey(key);
    }
}
