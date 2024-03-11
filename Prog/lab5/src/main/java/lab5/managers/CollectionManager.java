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

/**
 * Менеджер коллекции
 */
public class CollectionManager {
    private HashMap<Integer, Ticket> collection = new HashMap<>();
    private List<Integer> sortSequence = new ArrayList<>();

    private LocalDateTime initTime;
    private LocalDateTime lastUpdateTime;

    /**
     * Добавляет элемент в коллекцию
     * @param ticket элемент
     * @throws InvalidDataException возникает при наличии невалидных данных
     * @see Ticket
     */
    public void addTicket(Ticket ticket) throws InvalidDataException {
        collection.put(ticket.getId(), ticket);
        sortSequence.add(ticket.getId());
        sort();      
        save();  
    }

    /**
     * Загружает коллекцию из файла
     * @param fileName имя файла
     * @throws FileNotFoundException возникает при отсутствии файла
     * @throws IOException возникает про ошибке ввода/вывода
     * @throws InvalidDataException возникает при наличии невалидных данных
     */
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

        sort();
    }

    /**
     * Конвертирует коллекцию в объект класса {@link Tickets}
     * @return Объект класса {@link Tickets}
     */
    private Tickets toTickets() {
        Tickets tickets = new Tickets();
        for (int i = 0; i < sortSequence.size(); i++) {
            tickets.addTicket(collection.get(sortSequence.get(i)));
        }

        return tickets;
    }

    /**
     * Сохраняет коллекцию в файл
     * @param fileName имя файла для сохранения
     * @throws JsonProcessingException возникает при ошибке парсинга
     * @throws FileNotFoundException возникает при отсутствии файла
     */
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

    /**
     * Очищает коллекцию
     */
    public void clearCollection() {
        collection.clear();
        sortSequence.clear();
        save();
    }

    /**
     * Удаляет элемент коллекции по ключу
     * @param id ключ
     * @throws IdNotFoundException возникает при отсутствии элемента с заданным ключом
     */
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

    /**
     * Возвращает элемент коллекции по ключу
     * @param id ключ
     * @return элемент коллекции
     * @throws IdNotFoundException возникает при отсутствии элемента с заданным ключом
     * @see Ticket
     */
    public Ticket getTicketById(int id) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id + " не существует!"));
        }

        return collection.get(id);
    }

    /**
     * Заменяет элемент коллекции по ключу
     * @param id ключ
     * @param ticket новый элемент коллекции
     * @throws IdNotFoundException возникает при отсутствии элемента с заданным ключом
     * @see Ticket
     */
    public void changeTicketById(int id, Ticket ticket) throws IdNotFoundException {
        if (!collection.containsKey(id)) {
            throw new IdNotFoundException("Тикета с id=" + String.valueOf(id) + " не существует!");
        }

        collection.replace(id, ticket);
        save();
    }

    /**
     * Конвертирует элементы коллекции в список
     * @return список элементов коллекции
     * @see Ticket
     */
    public ArrayList<Ticket> toArray() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < sortSequence.size(); i++) {
            tickets.add(collection.get(sortSequence.get(i)));
        }
        return tickets;
    }

    /**
     * Удаляет элементы коллекции, меньшие чем заданный
     * @param ticket заданный элемент типа {@link Ticket}
     */
    public void removeLowerThanTicket(Ticket ticket) {
        while (collection.get(sortSequence.get(0)).compareTo(ticket) < 0) {
            collection.remove(sortSequence.get(0));
            sortSequence.remove(0);
            if (sortSequence.size() == 0) break;
        }
        save();
    }

    /**
     * Удаляет один элемент коллекции, значение поля event которого эквивалентно заданному
     * @param event заданное значение типа {@link Event}
     * @return возвращает true если был удален хотя бы один элемент, и false в противном случае
     */
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

    /**
     * Удаляет все элементы коллекции, id которых меньше чем заданный
     * @param id заданный id
     */
    public void removeLowerThanId(int id) {
        while(sortSequence.get(0) < id) {
            collection.remove(sortSequence.get(0));
            sortSequence.remove(0);
            if (sortSequence.size() == 0) break;
        }
        save();
    }

    /**
     * Возвращает список элементов коллекции, значение поля event которых больше заданного
     * @param event заданное значение event
     * @return список элементов коллекции, значение поля event которых больше заданного
     * @see Event
     */
    public List<Ticket> filterGreaterByEvent(Event event) {
        List<Ticket> sorted = new ArrayList<>();

        for (Ticket ticket : collection.values()) {
            if (ticket.getEvent().compareTo(event) > 0) {
                sorted.add(ticket);
            } 
        }
        return sorted;
    }

    /**
     * Возвращает список элементов типа {@link Event} в порядке убывания
     * @return список элементов типа {@link Event} в порядке убывания
     */
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

    /**
     * Сортирует коллекцию
     */
    private void sort() {
        Comparator<Integer> ticketsComparator = new TicketComparator(collection);
        Collections.sort(sortSequence, ticketsComparator);
    }

    /**
     * Возвращает список ключей коллекции
     * @return список ключей коллекции
     */
    public List<Integer> getKeys() {
        return sortSequence;
    }

    /**
     * Проверяет наличие элемента коллекции по ключу
     * @param key ключ
     * @return true если элемент присутствует, и false в противном случае
     */
    public boolean hasId(int key) {
        return collection.containsKey(key);
    }

    /**
     * Обновляет дату последнего изменения коллекции
     */
    private void save() {
        lastUpdateTime = LocalDateTime.now();
    }

    /**
     * Возврашает время инициализации коллекции в строковом представлении
     * @return время инициализации коллекции в строковом представлении
     */
    public String getInitTime() {
        return initTime.toString();
    }

    /**
     * Возвращает время последнего изменения коллекции в строковом представлении
     * @return время последнего изменения коллекции в строковом представлении
     */
    public String getLastUpdateTime() {
        return lastUpdateTime.toString();
    }

    /**
     * Возвращает тип коллекции в строковом представлении
     * @return тип коллекции в строковом представлении
     */
    public String getType() {
        return collection.getClass().toString();
    }

    /**
     * Возвращает размер коллекции
     * @return размер коллекции
     */
    public int getSize() {
        return collection.size();
    }
}
