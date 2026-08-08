package com.andrew;

import java.util.List;

import com.andrew.commands.ExclaimCommand;
import com.andrew.commands.IdleCommand;
import com.andrew.commands.ReadCommand;
import com.andrew.managers.Scenario;
import com.andrew.models.Person;
import com.andrew.models.Ship;
import com.andrew.models.TechnicalSpecification;

public class Main {
    public static void main(String[] args) {
        Person ford = new Person("Форд");

        Ship ship = new Ship("Корабль");

        TechnicalSpecification specs = new TechnicalSpecification("Спецификация", ship,
            List.of("Галактические технологии", "продвинулись далеко вперед"));
        ship.setSpecs(specs);

        Scenario scenario = new Scenario();

        scenario.addCommand(new IdleCommand(ford));
        scenario.addCommand(new ReadCommand(ford, ship.getSpecs()));
        scenario.addCommand(new ExclaimCommand(ford, "Восхитительно"));

        scenario.play();
    }
}