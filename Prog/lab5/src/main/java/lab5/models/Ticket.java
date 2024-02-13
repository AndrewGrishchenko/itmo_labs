package lab5.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Comparable<Ticket> {
    private static List<Integer> usedId = new ArrayList<>();
    private static int lastId = 0;

    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
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


    public Ticket(String name, Coordinates coordinates, int price, TicketType type, Event event) {
        this.id = getNextId();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
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
        if (!coordinates.validate()) return false;
        if (!event.validate()) return false;
        return true;
    }

    @Override
    public String toString() {
        String s = "Ticket{\n  id='" + String.valueOf(id) + "'\n  name='" + name + "'\n  coordinates={\n    x='" + String.valueOf(coordinates.getX())
        + "'\n    y='" + String.valueOf(coordinates.getY()) + "'}\n  price='" + String.valueOf(price) + "'\n  type='" + String.valueOf(type)
        + "'\n  event={\n    id='" + String.valueOf(event.getId()) + "'\n    name='" + event.getName() + "'\n    date='" + event.getDate().toString()
        + "'\n    ticketsCount='" + String.valueOf(event.getTicketsCount()) + "'\n    description='" + event.getDescription() + "'}\n";
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