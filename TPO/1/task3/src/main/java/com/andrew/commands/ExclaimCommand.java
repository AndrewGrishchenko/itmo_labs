package com.andrew.commands;

import com.andrew.enums.PersonState;
import com.andrew.models.Person;

public class ExclaimCommand extends Command {
    private final String message;

    public ExclaimCommand(Person actor, String message) {
        super(actor);
        this.message = message;
    }

    @Override
    public void execute() {
        Person actor = getActor();

        if (actor.getState() == PersonState.IMPRESSED) {
            actor.exclaim("[Восхищенно] " + message);
        } else {
            actor.exclaim(message);
        }
    }
}
