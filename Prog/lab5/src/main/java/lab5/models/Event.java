package lab5.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lab5.adapters.ScannerAdapter;

/**
 * Класс Event
 */
public class Event implements Comparable<Event> {
    @JsonIgnore
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private ZonedDateTime date; //Поле не может быть null
    private Long ticketsCount; //Поле может быть null, Значение поля должно быть больше 0
    private String description; //Поле может быть null

    /**
     * <p>Конструктор класса с установленным id=1</p>
     * <p>Не использовать для хранения в коллекции!</p>
     */
    public Event() {
        this.id = 1;
    }

    /**
     * Конструктор класса
     * @param id значение id
     */
    public Event(int id) {
        this.id = id;
    }

    /**
     * Конструктор класса с данными
     * @param name значение name
     * @param date значение date
     * @param ticketsCount значение ticketsCount
     * @param description значение description
     */
    public Event (String name, ZonedDateTime date, Long ticketsCount, String description) {
        this.name = name;
        this.date = date;
        this.ticketsCount = ticketsCount;
        this.description = description;
    }

    /**
     * Проверяет валидность данных
     * @return возвращает true если все данные валидны, false в противном случае
     */
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

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, ticketsCount, description);
    }

    /**
     * Заполняет данные объекта из консоли или скрипта
     */
    public void fillData() {
        this.setName(ScannerAdapter.getString("Введите name (String): "));
        this.setDate(ScannerAdapter.getZonedDateTime("Введите date (формат: 2020-01-23 15:30:55 Europe/Moscow): "));
        this.setTicketsCount(ScannerAdapter.getLong("Введите ticketsCount (int): "));
        this.setDescription(ScannerAdapter.getString("Введите description (String): "));
    }

    /**
     * Возвращает значение id
     * @return значение id
     */
    public Integer getId() {
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
        this.name = name;
    }

    /**
     * Возвращает значение date
     * @return значение date
     */
    public String getDate() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ").format(date) + date.getZone().toString();
    }

    /**
     * Устанавливает значение date
     * @param date значение date в строковом представлении
     * @throws DateTimeParseException возникает при ошибке парсинга даты
     */
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

    /**
     * Устанавливает значение date
     * @param zdt значение date
     */
    public void setDate(ZonedDateTime zdt) {
        this.date = zdt;
    }

    /**
     * Возвращает значение ticketsCount
     * @return значение ticketsCount
     */
    public Long getTicketsCount() {
        return ticketsCount;
    }

    /**
     * Устанавливает значение ticketsCount
     * @param ticketsCount значение ticketsCount
     */
    public void setTicketsCount(Long ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    /**
     * Возвращает значение description
     * @return значение description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает значение description
     * @param description значение description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}