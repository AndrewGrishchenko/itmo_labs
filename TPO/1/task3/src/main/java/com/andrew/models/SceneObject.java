package com.andrew.models;

public abstract class SceneObject {
    protected final String name;

    public SceneObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
