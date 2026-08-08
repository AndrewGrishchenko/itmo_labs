package com.andrew.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.andrew.models.Person;
import com.andrew.util.BaseTest;

public class IdleCommandTest extends BaseTest {
    @Test
    public void testIdleCommandExecution() {
        Person ford = new Person("Форд");

        IdleCommand cmd = new IdleCommand(ford);
        cmd.execute();

        assertTrue(getOutput().contains("Форд[IDLE]"));
    }
}
