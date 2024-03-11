package lab5.commands;

import java.util.Objects;

import lab5.interfaces.Executable;

/**
 * Абстракный класс команд
 */
public abstract class Command implements Executable {
    private final String name;
    private final String description;
    private final String usage;

    /**
     * Конструктор абстрактного класса команды
     * @param name наименование команды
     * @param description описание команды
     * @param usage использование команды
     */
    public Command(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Возвращает имя команды
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает описание команды
     * @return описание команды
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращает использование команды
     * @return использование команды
     */
    public String getUsage() {
        return "Использование: " + usage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return Objects.equals(name, other.name) && Objects.equals(description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return usage + ": " + description;
    }
}
