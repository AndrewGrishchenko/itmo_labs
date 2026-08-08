package com.andrew.commands;

import com.andrew.enums.PersonState;
import com.andrew.models.Person;

public class IdleCommand extends Command {
    public IdleCommand(Person actor) {
        super(actor);
    }

    @Override
    public void execute() {
        getActor().setState(PersonState.IDLE);
        
        System.out.println(getActor().getNameState());
    }
}
