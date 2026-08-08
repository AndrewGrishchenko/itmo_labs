package com.andrew.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.andrew.enums.PersonState;
import com.andrew.models.Person;
import com.andrew.util.BaseTest;

public class ExclaimCommandTest extends BaseTest {
    @Test
    public void testExclaimCommandIdle() {
        Person ford = new Person("Форд");

        ExclaimCommand cmd = new ExclaimCommand(ford, "Восхитительно");
        cmd.execute();

        assertTrue(getOutput().contains("восклицает: Восхитительно"));
        assertFalse(getOutput().contains("[Восхищенно]"));
    }

    @Test
    public void testExclaimCommandImpressed() {
        Person ford = new Person("Форд");
        ford.setState(PersonState.IMPRESSED);

        ExclaimCommand cmd = new ExclaimCommand(ford, "Восхитительно");
        cmd.execute();

        assertTrue(getOutput().contains("[Восхищенно] Восхитительно"));
    }
}
