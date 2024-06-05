package lab7_core.models;

import java.io.Serializable;
import java.util.Objects;

public class CommandMeta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final String usage;
    private final String requiredObject;
    private final boolean authRequired;
    private final int[] argC;

    public CommandMeta(String name, String description, String usage, String requiredObject, boolean authRequired, int... argC) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.requiredObject = requiredObject;
        this.authRequired = authRequired;
        this.argC = argC;
    }

    public String getName () {
        return name;
    }

    public String getDescription () {
        return "Использование: " + description;
    }

    public String getUsage () {
        return usage;
    }

    public String getRequiredObject () {
        return requiredObject;
    }

    public boolean isAuthRequired () {
        return authRequired;
    }

    public int[] getArgC () {
        return argC;
    }

    public boolean testArgC (int argC) {
        for (int arg : this.argC) {
            if (arg == argC) return true;
        }
        return false;
    }

    @Override
    public String toString () {
        return "CommandMeta{requiredObject=" + requiredObject + ", authRequired=" + String.valueOf(authRequired) + ", argC=" + argC + "}";
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CommandMeta other = (CommandMeta) obj;
        return Objects.equals(name, other.getName())
        && Objects.equals(description, other.getDescription())
        && Objects.equals(usage, other.getUsage())
        && Objects.equals(requiredObject, other.getRequiredObject())
        && Objects.equals(authRequired, other.isAuthRequired())
        && Objects.equals(argC, other.getArgC());
    }
}
