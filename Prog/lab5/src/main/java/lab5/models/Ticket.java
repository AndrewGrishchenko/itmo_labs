package lab5.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lab5.adapters.ConsoleAdapter;
import lab5.adapters.ScannerAdapter;
import lab5.exceptions.TooManyArgumentsException;

/**
 * Класс Ticket
 */
public class Ticket implements Comparable<Ticket> {
    private static List<Integer> usedId = new ArrayList<>();
    private static int lastId = 0;

    @JsonIgnore
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    @JsonIgnore
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private TicketType type; //Поле не может быть null
    private Event event; //Поле не может быть null

    /**
     * Конструктор класса
     */
    public Ticket() {
        this.id = getNextId();
        creationDate = LocalDateTime.now();
    }

    /**
     * Конструктор класса
     * @param id значение id
     */
    public Ticket(int id) {
        this.id = id;
        creationDate = LocalDateTime.now();
    }

    /**
     * Конструктор класса на основе другого объекта класса
     * @param another другой объект класса
     */
    public Ticket(Ticket another) {
        this.id = another.getId();
        this.name = another.getName();
        this.coordinates = another.getCoordinates();
        this.creationDate = another.getCreationDate();
        this.price = another.getPrice();
        this.type = another.getType();
        this.event = another.getEvent();
    }

    /**
     * Конструктор класса с данными
     * @param name значение name
     * @param coordinates значение coordinates
     * @param price значение price
     * @param type значение type
     * @param event значение event
     * @see Coordinates
     * @see TicketType
     * @see Event
     */
    public Ticket(String name, Coordinates coordinates, int price, TicketType type, Event event) {
        this.id = getNextId();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.price = price;
        this.type = type;
        this.event = event;
    }

    /**
     * Возвращает следующее свободное id
     * @return следующее свободное id
     */
    private static int getNextId() {
        int id = lastId + 1;
        boolean found = false;
        while (!found) {
            if (containsId(id)) id += 1;
            else found = true;
        }
        lastId = id;
        usedId.add(id);
        return id;
    }

    /**
     * Проверяет наличие заданного id
     * @param id значение id
     * @return возвращает true если такой id уже есть, false в противном случае
     */
    private static boolean containsId(int id) {
        for (int i = 0; i < usedId.size(); i++) {
            if (usedId.get(i) == id) return true;
        }
        return false;
    }

    /**
     * Проверяет валидность данных
     * @return возвращает true если все данные валидны, false в противном случае
     */
    public boolean validate()  {
        if (id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (price <= 0) return false;
        if (type == null) return false;
        if (event == null) return false;
        if (!coordinates.validate()) return false;
        if (!event.validate()) return false;
        return true;
    }

    @Override
    public String toString() {
        String s = "Ticket{\n  id='" + String.valueOf(id) + "'\n  name='" + name + "'\n  coordinates={\n    x='" + String.valueOf(coordinates.getX())
        + "'\n    y='" + String.valueOf(coordinates.getY()) + "'}\n  creationDate='" + getCreationDate().toString() + "\n  price='" + String.valueOf(price)
        + "'\n  type='" + String.valueOf(type) + "'\n  event={\n    id='" + String.valueOf(event.getId()) + "'\n    name='" + event.getName()
        + "'\n    date='" + event.getDate() + "'\n    ticketsCount='" + String.valueOf(event.getTicketsCount())
        + "'\n    description='" + event.getDescription() + "'}\n";
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        Ticket other = (Ticket) obj;
        return Objects.equals(getName(), other.getName())
        && Objects.equals(getCoordinates(), other.getCoordinates())
        && Objects.equals(getCreationDate(), other.getCreationDate())
        && Objects.equals(getPrice(), other.getPrice())
        && Objects.equals(getType(), other.getType())
        && Objects.equals(getEvent(), other.getEvent());
    }

    @Override
    public int compareTo(Ticket other) {
        return this.getPrice() - other.getPrice();
    }

    /**
     * Заполняет данные объекта из консоли или скрипта
     * @throws TooManyArgumentsException возникает при наличии слишком большого количества аргументов
     */
    public void fillData() throws TooManyArgumentsException {
        ConsoleAdapter.print("Введите name (String): ");
        this.setName(ScannerAdapter.getString());

        Coordinates coordinates = this.getCoordinates() == null ? new Coordinates() : this.getCoordinates();
        ConsoleAdapter.print("Введите coordinates.x (double): ");
        coordinates.setX(ScannerAdapter.getDouble());

        ConsoleAdapter.print("Введите coordinates.y (double): ");
        coordinates.setY(ScannerAdapter.getDouble());
        this.setCoordinates(coordinates);

        ConsoleAdapter.print("Введите price (int): ");
        this.setPrice(ScannerAdapter.getInt());

        ConsoleAdapter.print("Введите type (VIP, USUAL, BUDGETARY, CHEAP): ");
        this.setType(TicketType.valueOf(ScannerAdapter.getString()));

        Event event = this.getEvent() == null ? new Event() : this.getEvent();
        ConsoleAdapter.print("Введите event.name (String): ");
        event.setName(ScannerAdapter.getString());

        ConsoleAdapter.print("Введите event.date (формат: 2020-01-23 15:30:55 Europe/Moscow): ");
        event.setDate(ScannerAdapter.getString());

        ConsoleAdapter.print("Введите event.ticketsCount (int): ");
        event.setTicketsCount(ScannerAdapter.getLong());
        
        ConsoleAdapter.print("Введите description (String): ");
        event.setDescription(ScannerAdapter.getString());
        this.setEvent(event);
    }

    /**
     * Восстанавливает значения из старого объекта
     * @param oldTicket старый объект
     */
    public void restoreData(Ticket oldTicket) {
        this.name = oldTicket.getName();
        this.coordinates = oldTicket.getCoordinates();
        this.creationDate = oldTicket.getCreationDate();
        this.price = oldTicket.getPrice();
        this.type = oldTicket.getType();
        this.event = oldTicket.getEvent();
    }

    /**
     * Возвращает значение id
     * @return значение id
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает значение name
     * @return значение name
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает значение name
     * @param name значение name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает значение coordinates
     * @return значение coordinates
     * @see Coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Устанавливает значение coordinates
     * @param coordinates значение coordinates
     * @see Coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Возвращает время создания
     * @return время создания
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает значение price
     * @return значение price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Устанавливает значение price
     * @param price значение price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Возвращает значение type
     * @return значение type
     * @see TicketType
     */
    public TicketType getType() {
        return type;
    }

    /**
     * Устанавливает значение type
     * @param type значение type
     * @see TicketType
     */
    public void setType(TicketType type) {
        this.type = type;
    }

    /**
     * Возвращает значение event
     * @return значение event
     * @see Event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Устанавливает значение event
     * @param event значение event
     * @see Event
     */
    public void setEvent(Event event) {
        event.setId(id);
        this.event = event;
    }
}