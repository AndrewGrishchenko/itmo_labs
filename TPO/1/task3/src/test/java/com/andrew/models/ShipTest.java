package com.andrew.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ShipTest {
    @Test
    public void testShipCreation() {
        Ship ship = new Ship("Корабль");

        assertEquals("Корабль", ship.getName());
        assertNull(ship.getSpecs());
    }

    @Test
    public void testSetAndGetSpecs() {
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship, List.of("A", "B"));

        ship.setSpecs(specs);

        assertEquals(specs, ship.getSpecs());
    }
}
