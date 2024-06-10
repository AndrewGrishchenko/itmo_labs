package lab7_server.managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import lab7_server.exceptions.IdNotFoundException;
import lab7_core.exceptions.InvalidDataException;
import lab7_core.models.Event;
import lab7_core.models.Ticket;
import lab7_server.utility.EventComparator;
import lab7_server.utility.TicketComparator;

/**
 * Менеджер коллекции
 */
public class CollectionManager {
    private class CollectionObserver {
        private HashMap<Integer, Ticket> collection = new HashMap<>();
        
        public void put (Integer key, Ticket value) {
            DBManager.executeInsert("tickets", value);
            collection.put(key, value);
            sortSequence.add(value.getId());
            sort();
            save();
        }

        public Ticket get (Integer key) {
            return collection.get(key);
        }

        public void remove (Integer key) {
            collection.remove(key);
            //TODO: think of making rule of deleting values that belongs to current user
        }

        public Collection<Ticket> values () {
            return collection.values();
        }

        public boolean containsKey (Integer key) {
            return collection.containsKey(key);
        }

        public int size () {
            return collection.size();
        }

        public void replace (Integer key, Ticket value) {
            DBManager.updateTicket(value);
            collection.replace(key, value);
        }

        public HashMap<Integer, Ticket> getCollection () {
            return collection;
        }

        public void dump (List<Ticket> tickets) {
            tickets.stream().forEach((ticket) -> {
                collection.put(ticket.getId(), ticket);
                sortSequence.add(ticket.getId());
            });
            sort();
        }
    }

    // private HashMap<Integer, Ticket> collection = new HashMap<>();
    private CollectionObserver collection = new CollectionObserver();
    private List<Integer> sortSequence = new ArrayList<>();

    private LocalDateTime initTime;
    private LocalDateTime lastUpdateTime;

    public CollectionManager () {
    }

    /**
     * Добавляет элемент в коллекцию
     * @param ticket элемент
     * @throws InvalidDataException возникает при наличии невалидных данных
     * @see Ticket
     */
    public void addTicket(Ticket ticket) throws InvalidDataException {
        collection.put(ticket.getId(), ticket);
    }

    public boolean replaceTicket (Ticket oldTicket, Ticket newTicket, int userId) {
        newTicket.setId(oldTicket.getId());
        Event event = newTicket.getEvent();
        event.setId(oldTicket.getEvent().getId());
        newTicket.setEvent(event);


        if (oldTicket.getCreatorId() == userId) {
            collection.replace(oldTicket.getId(), newTicket);
            return true;
        }
        return false;
    }

    public boolean removeTicketByUser (Integer key, int userId) {
        if (collection.get(key).getCreatorId() == userId) {
            DBManager.deleteTicket(key);
            for (int i = 0; i < sortSequence.size(); i++) {
                if (sortSequence.get(i) == collection.get(key).getId()) {
                    sortSequence.remove(i);
                    save();
                }
            }
            collection.remove(key);
            return true;
        }
        return false;
    }

    /**
     * Загружает коллекцию из файла
     * @param fileName имя файла
     * @throws FileNotFoundException возникает при отсутствии файла
     * @throws IOException возникает про ошибке ввода/вывода
     * @throws InvalidDataException возникает при наличии невалидных данных
     */
    public void dumpData() throws FileNotFoundException, IOException, InvalidDataException {
        initTime = LocalDateTime.now();
        lastUpdateTime = initTime;
        
        collection.dump(DBManager.executeSelect("tickets").stream()
        .map((Object ticket) -> ((Ticket) ticket))
        .toList());

        // FileInputStream fileInputStream = new FileInputStream(fileName);
        // InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        // Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
        // String xml = scanner.hasNext() ? scanner.next() : "";
        
        // scanner.close();
        // inputStreamReader.close();
        // fileInputStream.close();

        // XmlMapper xmlMapper = new XmlMapper();

        // Tickets map = xmlMapper.readValue(xml, Tickets.class);
        // List<Ticket> ticketsList = map.getTicket();
        // for (int i = 0; i < ticketsList.size(); i++) {
        //     collection.put(ticketsList.get(i).getId(), ticketsList.get(i));
        //     sortSequence.add(ticketsList.get(i).getId());
        // }

        sort();
    }

    /**
     * Сохраняет коллекцию в файл
     * @param fileName имя файла для сохранения
     * @throws JsonProcessingException возникает при ошибке парсинга
     * @throws FileNotFoundException возникает при отсутствии файла
     */
    public void saveData() throws JsonProcessingException, FileNotFoundException {
        // PrintWriter printWriter = new PrintWriter(fileName);
        // if (collection.isEmpty()) printWriter.println();
        // else {
        //     XmlMapper xmlMapper = new XmlMapper();
        //     xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //     xmlMapper.addMixIn(Ticket.class, TicketMixin.class);

        //     Tickets tickets = toTickets();            
        //     String xml = xmlMapper.writeValueAsString(tickets);

        //     printWriter.println(xml);
        // }
        // printWriter.close();
        
    }

    /**
     * Очищает коллекцию
     */
    // public void clearCollection() {
        // collection.clear();
        // sortSequence.clear();
        // save();
    // }
    public Collection<Ticket> getValues () {
        return collection.getCollection().values();
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
        Comparator<Integer> ticketsComparator = new TicketComparator(collection.getCollection());
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
