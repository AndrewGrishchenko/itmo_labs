package com.andrew.models;

public class Ship extends SceneObject {
    private TechnicalSpecification specs;

    public Ship(String name) {
        super(name);
    }

    public void setSpecs(TechnicalSpecification specs) {
        this.specs = specs;
    }

    public TechnicalSpecification getSpecs() {
        return specs;
    }
}
