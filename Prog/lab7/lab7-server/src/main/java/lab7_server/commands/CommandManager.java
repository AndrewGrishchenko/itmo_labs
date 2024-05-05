package lab7_server.commands;

import java.util.ArrayList;

public class CommandManager {
    private ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand (Command command) {
        commands.add(command);
    }

    public ArrayList<Command> getCommands () {
        return commands;
    }

    public Command getCommand (String name) {
        for (Command command : commands) {
            if (command.getName().equals(name)) return command;
        }
        return null;
    }
}
