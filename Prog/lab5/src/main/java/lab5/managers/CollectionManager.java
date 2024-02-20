package lab5.managers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
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
import lab5.models.Event;
import lab5.models.Ticket;
import lab5.models.Tickets;
import lab5.utility.EventComparator;
import lab5.utility.TicketComparator;

public class CollectionManager {
    private HashMap<Integer, Ticket> collection = new HashMap<>();
    private List<Integer> sortSequence = new ArrayList<>();

    private LocalDateTime initTime;
    private LocalDateTime lastUpdateTime;

    public void addTicket(Ticket ticket) throws InvalidDataException {
        collection.put(ticket.getId(), ticket);
        sortSequence.add(ticket.getId());
        validateAll();
        sort();      
        save();  
    }

    public void dumpData(String fileName) throws FileNotFoundException, IOException, InvalidDataException {
        initTime = LocalDateTime.now();
        lastUpdateTime = initTime;
        
        FileInputStream fileInputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
        String xml = scanner.hasNext() ? scanner.next() : "";
        
        scanner.close();
        inputStreamReader.close();
        fileInputStream.close();

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
        PrintWriter printWriter = new PrintWriter(fileName);
        if (collection.isEmpty()) printWriter.println();
        else {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            Tickets tickets = toTickets();
            String xml = xmlMapper.writeValueAsString(tickets);

            printWriter.println(xml);
        }
        printWriter.close();
    }

    public void clearCollection() {
        collection.clear();
        sortSequence.clear();
        save();
    }

    public void removeTicketById(int id) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id) + " не существует!");
        }
        collection.remove(id);
        for (int i = 0; i < sortSequence.size(); i++) {
            if (sortSequence.get(i) == id) {
                sortSequence.remove(i);
                save();
                return;
            }
        }
    }

    public Ticket getTicketById(int id) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id + " не существует!"));
        }

        return collection.get(id);
    }

    public void changeTicketById(int id, Ticket ticket) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id) + " не существует!");
        }

        collection.replace(id, ticket);
        save();
    }

    public ArrayList<Ticket> toArray() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < sortSequence.size(); i++) {
            tickets.add(collection.get(sortSequence.get(i)));
        }
        return tickets;
    }

    public void removeLowerThanTicket(Ticket ticket) {
        while (collection.get(sortSequence.get(0)).compareTo(ticket) < 0) {
            collection.remove(sortSequence.get(0));
            sortSequence.remove(0);
            if (sortSequence.size() == 0) break;
        }
        save();
    }

    public boolean removeOneByEvent(Event event) {
        for (int i = 0; i < sortSequence.size(); i++) {
            if (event.equals(collection.get(sortSequence.get(i)).getEvent())) {
                collection.remove(collection.get(sortSequence.get(i)).getId());
                sortSequence.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    public void removeLowerThanId(int id) {
        while(sortSequence.get(0) < id) {
            collection.remove(sortSequence.get(0));
            sortSequence.remove(0);
            if (sortSequence.size() == 0) break;
        }
        save();
    }

    public List<Ticket> filterGreaterByEvent(Event event) {
        List<Ticket> sorted = new ArrayList<>();

        for (Ticket ticket : collection.values()) {
            if (ticket.getEvent().compareTo(event) > 0) {
                sorted.add(ticket);
            } 
        }
        return sorted;
    }

    public List<Event> sortedDescendingEvents() {
        List<Event> events = new ArrayList<>();
        for (Ticket ticket : collection.values()) {
            events.add(ticket.getEvent());
        }

        Comparator<Event> eventComparator = new EventComparator();
        Collections.sort(events, eventComparator);
        Collections.reverse(events);
        return events;
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
        Comparator<Integer> ticketsComparator = new TicketComparator(collection);
        Collections.sort(sortSequence, ticketsComparator);
    }

    public int size() {
        return collection.size();
    }

    public List<Integer> getKeys() {
        return sortSequence;
    }

    public boolean hasId(int key) {
        return collection.containsKey(key);
    }

    private void save() {
        lastUpdateTime = LocalDateTime.now();
    }

    public String getInitTime() {
        return initTime.toString();
    }

    public String getLastUpdateTime() {
        return lastUpdateTime.toString();
    }

    public String getType() {
        return collection.getClass().toString();
    }

    public int getSize() {
        return collection.size();
    }
}
