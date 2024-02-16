package lab5.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lab5.adapters.ScannerAdapter;
import lab5.exceptions.TooManyArgumentsException;
import lab5.utility.console.Console;

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

    public Ticket() {
        this.id = getNextId();
        creationDate = LocalDateTime.now();
    }

    public Ticket(int id) {
        this.id = id;
        creationDate = LocalDateTime.now();
    }

    public Ticket(Ticket another) {
        this.id = another.getId();
        this.name = another.getName();
        this.coordinates = another.getCoordinates();
        this.creationDate = another.getCreationDate();
        this.price = another.getPrice();
        this.type = another.getType();
        this.event = another.getEvent();
    }

    public Ticket(String name, Coordinates coordinates, int price, TicketType type, Event event) {
        this.id = getNextId();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.price = price;
        this.type = type;
        this.event = event;
    }

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

    private static boolean containsId(int id) {
        for (int i = 0; i < usedId.size(); i++) {
            if (usedId.get(i) == id) return true;
        }
        return false;
    }

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

    public void fillData(Console console) throws TooManyArgumentsException {
        console.print("Введите name (String): ");
        this.setName(ScannerAdapter.getString());

        Coordinates coordinates = this.getCoordinates() == null ? new Coordinates() : this.getCoordinates();
        console.print("Введите coordinates.x (double): ");
        coordinates.setX(ScannerAdapter.getDouble());

        console.print("Введите coordinates.y (double): ");
        coordinates.setY(ScannerAdapter.getDouble());
        this.setCoordinates(coordinates);

        console.print("Введите price (int): ");
        this.setPrice(ScannerAdapter.getInt());

        console.print("Введите type (VIP, USUAL, BUDGETARY, CHEAP): ");
        this.setType(TicketType.valueOf(ScannerAdapter.getString()));

        Event event = this.getEvent() == null ? new Event() : this.getEvent();
        console.print("Введите event.name (String): ");
        event.setName(ScannerAdapter.getString());

        console.print("Введите event.date (формат: 2020-01-23 15:30:55 Europe/Moscow): ");
        event.setDate(ScannerAdapter.getString());

        console.print("Введите event.ticketsCount (int): ");
        event.setTicketsCount(ScannerAdapter.getLong());
        
        console.print("Введите description (String): ");
        event.setDescription(ScannerAdapter.getString());
        this.setEvent(event);
    }

    public void restoreData(Ticket oldTicket) {
        this.name = oldTicket.getName();
        this.coordinates = oldTicket.getCoordinates();
        this.creationDate = oldTicket.getCreationDate();
        this.price = oldTicket.getPrice();
        this.type = oldTicket.getType();
        this.event = oldTicket.getEvent();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        event.setId(id);
        this.event = event;
    }
}