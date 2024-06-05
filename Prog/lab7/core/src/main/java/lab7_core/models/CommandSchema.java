package lab7_core.models;

import java.io.Serializable;
import java.util.HashMap;

public class CommandSchema implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, CommandMeta> commands = new HashMap<>();

    public void addCommand(String command, CommandMeta meta) {
        commands.put(command, meta);
    }

    public CommandMeta getMeta (String command) {
        return commands.get(command);
    }

    @Override
    public String toString () {
        String s = "CommandSchema{";
        for (String command : commands.keySet()) {
            s += command + ": " + commands.get(command).toString() + ",\n";
        }
        return s;
    }
}