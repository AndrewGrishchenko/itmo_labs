package lab5.managers;

import java.util.ArrayList;

import lab5.commands.Command;

public class CommandManager {
    private final ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public Command getCommand(String name) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(name)) return commands.get(i);
        }
        return null;
    }
}
