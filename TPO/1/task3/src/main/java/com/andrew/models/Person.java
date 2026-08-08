package com.andrew.models;

import com.andrew.enums.PersonState;

public class Person extends SceneObject {
    private PersonState state = PersonState.IDLE;
    
    public Person(String name) {
        super(name);
    }

    public PersonState getState() {
        return state;
    }

    public void setState(PersonState state) {
        this.state = state;
    }

    public void exclaim(String message) {
        System.out.println(getNameState() + " восклицает: " + message);
    }

    public String getNameState() {
        return name + "[" + getState() + "]";
    }
}
