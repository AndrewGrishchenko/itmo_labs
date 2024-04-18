package lab6_core.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lab6_core.adapters.ScannerAdapter;
import lab6_core.exceptions.InvalidDataException;

/**
 * Класс Ticket
 */
public class Ticket implements Serializable, Comparable<Ticket> {
    private static final long serialVersionUID = 1L;
    
    private static List<Integer> usedId = new ArrayList<>();
    private static int lastId = 0;

    private int filledData = 1;

    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
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
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, price, type, event);
    }

    @Override
    public int compareTo(Ticket other) {
        return this.getPrice() - other.getPrice();
    }

    /**
     * Заполняет данные объекта из консоли или скрипта
     */
    public void fillData() {
        Coordinates coordinates = this.getCoordinates() == null ? new Coordinates() : this.getCoordinates();
        Event event = this.getEvent() == null ? new Event() : this.getEvent();
        while (filledData != 10) {
            // try {
                switch (filledData) {
                    case 1: this.setName(ScannerAdapter.getString("Введите name (String): "));
                            break;
                    case 2: coordinates.setX(ScannerAdapter.getPrimitiveDouble("Введите coordinates.x (double): "));
                            break;
                    case 3: coordinates.setY(ScannerAdapter.getDouble("Введите coordinates.y (Double): "));
                            this.setCoordinates(coordinates);
                            break;
                    case 4: this.setPrice(ScannerAdapter.getPrimitiveInt("Введите price (int): "));
                            break;
                    case 5: this.setType(ScannerAdapter.getTicketType("Введите type (VIP, USUAL, BUDGETARY, CHEAP): "));
                            break;
                    case 6: event.setName(ScannerAdapter.getString("Введите event.name (String): "));
                            break;
                    case 7: event.setDate(ScannerAdapter.getZonedDateTime("Введите event.date (формат: 2020-01-23 15:30:55 Europe/Moscow): "));
                            break;
                    case 8: event.setTicketsCount(ScannerAdapter.getLong("Введите event.ticketsCount (Long): "));
                            break;
                    case 9: event.setDescription(ScannerAdapter.getString("Введите description (String): "));
                            this.setEvent(event);
                            break;
                }
                filledData++;
            // } catch (InvalidDataException e) {
                // ConsoleAdapter.printErr(e.getMessage());
            // }
        }
    }

    public ValueChecker fillPartly (String[] line) {
        ScannerAdapter.setFillMode();
        ScannerAdapter.fill(line);
        String msg = "";

        try {
            Coordinates coordinates = this.getCoordinates() == null ? new Coordinates() : this.getCoordinates();
            Event event = this.getEvent() == null ? new Event() : this.getEvent();

            switch (filledData) {
                case 1: msg = "Введите name (String): ";
                        this.setName(ScannerAdapter.getString(null));
                        break;
                case 2: msg = "Введите coordinates.x (double): ";
                        coordinates.setX(ScannerAdapter.getPrimitiveDouble(null));
                        break;
                case 3: msg = "Введите coordinates.y (Double): ";
                        coordinates.setY(ScannerAdapter.getDouble(null));
                        this.setCoordinates(coordinates);
                        break;
                case 4: msg = "Введите price (int): ";
                        this.setPrice(ScannerAdapter.getPrimitiveInt(null));
                        break;
                case 5: msg = "Введите type (VIP, USUAL, BUDGETARY, CHEAP): ";
                        this.setType(ScannerAdapter.getTicketType(null));
                        break;
                case 6: msg = "Введите event.name (String): ";
                        event.setName(ScannerAdapter.getString(null));
                        break;
                case 7: msg = "Введите event.date (формат: 2020-01-23 15:30:55 Europe/Moscow): ";
                        event.setDate(ScannerAdapter.getZonedDateTime(null));
                        break;
                case 8: msg = "Введите event.ticketsCount (Long): ";
                        event.setTicketsCount(ScannerAdapter.getLong(null));
                        break;
                case 9: msg = "Введите description (String): ";
                        event.setDescription(ScannerAdapter.getString(null));
                        this.setEvent(event);
                        break;
            }
            filledData++;

            return filledData == 10 ? new ValueChecker(true, msg) : new ValueChecker(false, msg);
        } catch (InvalidDataException e) {
            return new ValueChecker(false, msg, e.getMessage());
        }
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
     * Устанавливает значение id
     * @param id значение id
     */
    public void setId(int id) {
        this.id = id;
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
        if (name == null || name.isEmpty()) throw new InvalidDataException("Ticket.name");
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
        if (coordinates == null) throw new InvalidDataException("Ticket.coordinates");
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
        if (price <= 0) throw new InvalidDataException("Ticket.price");
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
        if (type == null) throw new InvalidDataException("Ticket.type");
        this.type = type;
    }

    /**
     * Устанавливает значение type
     * @param type значение type в строковом представлении
     */
    public void setType(String type) {
        TicketType ticketType = TicketType.valueOf(type);
        if (ticketType == null) throw new InvalidDataException("Ticket.type");
        this.type = ticketType;
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
        if (event == null) throw new InvalidDataException("Ticket.event");
        event.setId(id);
        this.event = event;
    }
}
