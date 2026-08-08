package com.andrew.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.andrew.commands.ExclaimCommand;
import com.andrew.commands.IdleCommand;
import com.andrew.commands.ReadCommand;
import com.andrew.enums.PersonState;
import com.andrew.models.Person;
import com.andrew.models.Ship;
import com.andrew.models.TechnicalSpecification;
import com.andrew.util.BaseTest;

public class ScenarioTest extends BaseTest {
    @Test
    public void testScenarioExecutionOrder() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("A", "продвинулись"));

        Scenario scenario = new Scenario();

        scenario.addCommand(new IdleCommand(ford));
        scenario.addCommand(new ReadCommand(ford, specs));
        scenario.addCommand(new ExclaimCommand(ford, "Восхитительно"));

        scenario.play();

        String out = getOutput();

        int idle = out.indexOf("IDLE");
        int read = out.indexOf("читает");
        int exclaim = out.indexOf("восклицает");

        assertTrue(idle < read && read < exclaim);
    }

    @Test
    public void testScenarioMultipleCommands() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("A"));

        Scenario scenario = new Scenario();

        scenario.addCommand(new IdleCommand(ford));
        scenario.addCommand(new ReadCommand(ford, specs));
        scenario.addCommand(new ExclaimCommand(ford, "OK"));

        scenario.play();

        String out = getOutput();

        assertTrue(out.contains("IDLE"));
        assertTrue(out.contains("читает"));
        assertTrue(out.contains("восклицает"));
    }

    @Test
    public void testScenarioStateConsistency() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("продвинулись далеко вперед"));

        Scenario scenario = new Scenario();

        scenario.addCommand(new ReadCommand(ford, specs));

        scenario.play();

        assertEquals(PersonState.IMPRESSED, ford.getState());
    }

    @Test
    public void testFullScenarioFordReadingShipSpecs() {
        Person ford = new Person("Форд");
        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs =
            new TechnicalSpecification("Спецификация", ship,
                List.of("Галактические технологии", "продвинулись далеко вперед"));

        ship.setSpecs(specs);

        Scenario scenario = new Scenario();

        scenario.addCommand(new IdleCommand(ford));
        scenario.addCommand(new ReadCommand(ford, specs));
        scenario.addCommand(new ExclaimCommand(ford, "Восхитительно"));

        scenario.play();

        String out = getOutput();

        assertTrue(out.contains("Форд[IDLE]"));
        assertTrue(out.contains("читает"));
        assertTrue(out.contains("восклицает"));
        assertTrue(ford.getState() == PersonState.IMPRESSED);
    }
}
