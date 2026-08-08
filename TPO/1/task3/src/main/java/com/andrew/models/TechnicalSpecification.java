package com.andrew.models;

import java.util.List;

import com.andrew.interfaces.Learnable;

public class TechnicalSpecification extends SceneObject implements Learnable {
    private final Ship ship;
    private final List<String> parts;
    private int index = 0;
    
    public TechnicalSpecification(String name, Ship ship, List<String> parts) {
        super(name);
        this.ship = ship;
        this.parts = parts;
    }

    public Ship getShip() {
        return ship;
    }

    @Override
    public boolean hasNext() {
        return index < parts.size();
    }

    @Override
    public String readNext() {
        return parts.get(index++);
    }
}
