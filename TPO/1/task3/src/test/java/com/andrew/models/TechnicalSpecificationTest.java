package com.andrew.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TechnicalSpecificationTest {
    @Test
    public void testSpecsCreation() {
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship, List.of("A", "B"));

        assertEquals("Спецификация", specs.getName());
        assertEquals(ship, specs.getShip());
    }

    @Test
    public void testReadNext() {
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship, List.of("A", "B"));

        assertEquals("A", specs.readNext());
        assertEquals("B", specs.readNext());
    }

    @Test
    public void testHasNext() {
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship, List.of("A"));

        assertTrue(specs.hasNext());
        specs.readNext();
        assertFalse(specs.hasNext());
    }

    @Test
    public void testReadExhaustion() {
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship, List.of("A"));

        specs.readNext();

        assertFalse(specs.hasNext());
    }

    
}
