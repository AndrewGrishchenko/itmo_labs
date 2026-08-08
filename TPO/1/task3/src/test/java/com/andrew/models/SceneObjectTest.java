package com.andrew.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SceneObjectTest {
    @Test
    public void testSceneObjectCreation() {
        SceneObject obj = new SceneObject("Объект") {};

        assertEquals("Объект", obj.getName());
    }
}
