package com.andrew.commands;

import com.andrew.enums.PersonState;
import com.andrew.interfaces.Learnable;
import com.andrew.models.Person;
import com.andrew.models.SceneObject;
import com.andrew.models.TechnicalSpecification;

public class ReadCommand extends Command {
    private final Learnable target;

    public ReadCommand(Person actor, Learnable target) {
        super(actor);
        this.target = target;
    }

    @Override
    public void execute() {
        Person actor = getActor();

        while (target.hasNext()) {
            String part = target.readNext();

            actor.setState(PersonState.READING);

            if (part.contains("продвинулись"))
                actor.setState(PersonState.IMPRESSED);

            String message = actor.getNameState() + " читает";
            if (target instanceof SceneObject obj)
                message += " \"" + obj.getName() + "\"";
            if (target instanceof TechnicalSpecification specs)
                message += " корабля \"" + specs.getShip().getName() + "\"";
            message += ": \"" + part + "\"";
            
            System.out.println(message);
        }
    }
}
