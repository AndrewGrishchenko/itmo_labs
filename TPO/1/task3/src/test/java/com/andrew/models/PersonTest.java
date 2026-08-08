package com.andrew.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.andrew.enums.PersonState;
import com.andrew.util.BaseTest;

public class PersonTest extends BaseTest {
    @Test
    public void testPersonCreation() {
        Person p = new Person("Форд");

        assertEquals("Форд", p.getName());
        assertEquals(PersonState.IDLE, p.getState());
    }

    @Test
    public void testSetState() {
        Person p = new Person("Форд");

        p.setState(PersonState.READING);
        assertEquals(PersonState.READING, p.getState());

        p.setState(PersonState.IMPRESSED);
        assertEquals(PersonState.IMPRESSED, p.getState());
    }

    @Test
    public void testNameStateFormat() {
        Person p = new Person("Форд");

        assertEquals("Форд[IDLE]", p.getNameState());

        p.setState(PersonState.READING);
        assertEquals("Форд[READING]", p.getNameState());
    }

    @Test
    public void testExclaimOutputFormat() {
        Person p = new Person("Форд");
        p.setState(PersonState.IMPRESSED);

        p.exclaim("Восхитительно");

        assertTrue(getOutput().contains("Форд[IMPRESSED] восклицает: Восхитительно"));
    }
}
