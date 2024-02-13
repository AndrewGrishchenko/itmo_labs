package lab5.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private static List<Integer> usedId = new ArrayList<>();
    private static int lastId = 0;

    private final Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private ZonedDateTime date; //Поле не может быть null
    private Long ticketsCount; //Поле может быть null, Значение поля должно быть больше 0
    private String description; //Поле может быть null

    public Event () {
        this.id = getNextId();
    }

    public Event (String name, ZonedDateTime date, Long ticketsCount, String description) {
        this.id = getNextId();
        this.name = name;
        this.date = date;
        this.ticketsCount = ticketsCount;
        this.description = description;
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

    public boolean validate() {
        if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (date == null) return false;
        if (ticketsCount <= 0) return false;
        return true;
    }

    public Integer getId() {
        return id;
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

    public void setDate(String date) {
        if (date == null) {
            this.date = null;
            return;
        }
        String[] parts = date.split(" ");
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