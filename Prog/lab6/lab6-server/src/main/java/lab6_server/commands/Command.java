package lab6_server.commands;

import java.util.Objects;

import lab6_server.interfaces.Runnable;
import lab6_server.interfaces.Validatable;

public abstract class Command implements Runnable, Validatable {
    private final String name;
    private final String description;
    private final String usage;
    private final String requiredObject;
    
    private String[] args;
    private Object obj;

    public Command (String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.requiredObject = null;
    }

    public Command (String name, String description, String usage, String requiredObject) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.requiredObject = requiredObject;
    }

    public String getName () {
        return name;
    }

    public String getDescription () {
        return description;
    }

    public String getUsage () {
        return "Использование: " + usage;
    }

    public String getRequiredObject () {
        return requiredObject;
    }

    public String[] getArgs () {
        return args;
    }

    public void setArgs (String[] args) {
        this.args = args;
    }

    public Object getObj () {
        return obj;
    }

    public void setObj (Object obj) {
        this.obj = obj;
    }

    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command other = (Command) obj;
        return Objects.equals(name, other.name)
            && Objects.equals(description, other.description) 
            && Objects.equals(usage, other.usage);
    }

    @Override
    public String toString () {
        return usage + ": " + description;
    }
}
