package com.andrew.managers;

import java.util.ArrayList;
import java.util.List;

import com.andrew.commands.Command;

public class Scenario {
    private final List<Command> commands = new ArrayList<>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void play() {
        for (Command command : commands) {
            command.execute();
        }
    }
}
