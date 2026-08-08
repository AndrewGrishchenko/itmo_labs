package com.andrew.commands;

import com.andrew.models.Person;

public abstract class Command {
    private final Person actor;

    public Command(Person actor) {
        this.actor = actor;
    }

    public Person getActor() {
        return actor;
    }

    public abstract void execute();
}
