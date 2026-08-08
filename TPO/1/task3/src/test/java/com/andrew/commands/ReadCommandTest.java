package com.andrew.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.andrew.enums.PersonState;
import com.andrew.models.Person;
import com.andrew.models.Ship;
import com.andrew.models.TechnicalSpecification;
import com.andrew.util.BaseTest;

public class ReadCommandTest extends BaseTest {
    @Test
    public void testReadCommandExecutionBasic() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("Технологии"));

        ReadCommand cmd = new ReadCommand(ford, specs);

        cmd.execute();

        assertTrue(getOutput().contains("читает"));
    }

    @Test
    public void testReadCommandStateTransitionReadingToImpressed() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("Галактические технологии", "продвинулись далеко вперед"));

        ReadCommand cmd = new ReadCommand(ford, specs);

        cmd.execute();

        assertEquals(PersonState.IMPRESSED, ford.getState());
    }

    @Test
    public void testReadCommandOutputContainsShipName() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Золотое сердце");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("Текст"));

        ReadCommand cmd = new ReadCommand(ford, specs);

        cmd.execute();

        assertTrue(getOutput().contains("Золотое сердце"));
    }

    @Test
    public void testReadCommandOutputContainsSpecificationName() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация XYZ", ship,
                List.of("Текст"));

        ReadCommand cmd = new ReadCommand(ford, specs);

        cmd.execute();

        assertTrue(getOutput().contains("Спецификация XYZ"));
    }

    @Test
    public void testReadCommandProcessesAllParts() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("A", "B", "C"));

        ReadCommand cmd = new ReadCommand(ford, specs);

        cmd.execute();

        String out = getOutput();

        assertTrue(out.contains("A"));
        assertTrue(out.contains("B"));
        assertTrue(out.contains("C"));
    }
}
