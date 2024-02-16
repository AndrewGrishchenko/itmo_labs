package lab5.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lab5.adapters.ScannerAdapter;
import lab5.exceptions.TooManyArgumentsException;
import lab5.utility.console.Console;

public class Event implements Comparable<Event> {

    @JsonIgnore
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private ZonedDateTime date; //Поле не может быть null
    private Long ticketsCount; //Поле может быть null, Значение поля должно быть больше 0
    private String description; //Поле может быть null

    public Event() {
        this.id = 1;
    }

    public Event(int id) {
        this.id = id;
    }

    public Event (String name, ZonedDateTime date, Long ticketsCount, String description) {
        this.name = name;
        this.date = date;
        this.ticketsCount = ticketsCount;
        this.description = description;
    }

    public boolean validate() {
        if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (date == null) return false;
        if (ticketsCount <= 0) return false;
        return true;
    }

    @Override
    public int compareTo(Event other) {
        return (int) (this.ticketsCount - other.getTicketsCount());
    }

    @Override
    public String toString() {
        String s = "Event{\n  id='" + String.valueOf(getId()) + "'\n  name='" + getName() + "'\n  date='" + getDate()
        + "'\n  ticketsCount='" + String.valueOf(getTicketsCount()) + "'\n  description='" + getDescription() + "'}\n";
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        Event other = (Event) obj;
        return Objects.equals(getName(), other.getName())
        && Objects.equals(getDate(), other.getDate())
        && Objects.equals(getTicketsCount(), other.getTicketsCount())
        && Objects.equals(getDescription(), other.getDescription());
    }

    public void fillData(Console console) throws TooManyArgumentsException {
        console.print("Введите name (String): ");
        this.setName(ScannerAdapter.getString());

        console.print("Введите date (формат: 2020-01-23 15:30:55 Europe/Moscow): ");
        this.setDate(ScannerAdapter.getString());

        console.print("Введите ticketsCount (int): ");
        this.setTicketsCount(ScannerAdapter.getLong());
        
        console.print("Введите description (String): ");
        this.setDescription(ScannerAdapter.getString());
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ").format(date) + date.getZone().toString();
    }

    public void setDate(String date) throws DateTimeParseException {
        if (date == null) {
            this.date = null;
            return;
        }
        String[] parts = date.split(" ");
        if (parts.length != 3) {
            throw new DateTimeParseException("", date, 0);
        }
        
        LocalDateTime ldt = LocalDateTime.parse(parts[0] + "T" + parts[1]);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(parts[2]));
        this.date = zdt;
    }

    public Long getTicketsCount() {
        return ticketsCount;
    }

    public void setTicketsCount(Long ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}