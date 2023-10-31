package Pokemons;
import ru.ifmo.se.pokemon.*;
import SpecialMoves.*;
import StatusMoves.*;

public class Wooper extends Pokemon {
    public Wooper(String name, int level) {
        super(name, level);
        setType(Type.WATER, Type.GROUND);
        setStats(4, 3, 3, 2, 2, 1);
        setMove(new LightScreen(), new ThunderShock(), new ThunderWave(), new Thunderbolt());
    }
}
