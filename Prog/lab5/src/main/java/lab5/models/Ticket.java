package lab5.models;

import java.time.LocalDateTime;

public class Ticket implements Comparable<Ticket> {
    private static int nextId = 1;

    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private TicketType type; //Поле не может быть null
    private Event event; //Поле не может быть null

    public Ticket() {
        this.id = nextId;
        nextId++;
        creationDate = LocalDateTime.now();
    }

    public Ticket(String name, Coordinates coordinates, LocalDateTime creationDate, int price, TicketType type, Event event) {
        this.id = nextId;
        nextId++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.type = type;
        this.event = event;
    }

    public boolean validate() {
        if (id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (price <= 0) return false;
        if (type == null) return false;
        if (event == null) return false;
        return true;
    }

    @Override
    public String toString() {
        String s = "Ticket{\n  id='" + String.valueOf(id) + "'\n  name='" + name + "'\n  coordinates={\n    x='" + String.valueOf(coordinates.getX())
        + "'\n    y='" + String.valueOf(coordinates.getY()) + "'}\n  price='" + String.valueOf(price) + "'\n  type='" + String.valueOf(type)
        + "'\n  event={\n    id='" + String.valueOf(event.getId()) + "'\n    name='" + event.getName() + "'\n    ticketsCount='" + String.valueOf(event.getTicketsCount())
        + "'\n    description='" + event.getDescription() + "'}\n";
        return s; 
    }

    @Override
    public int compareTo(Ticket other) {
        return this.getPrice() - other.getPrice();
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
        this.event = event;
    }
}