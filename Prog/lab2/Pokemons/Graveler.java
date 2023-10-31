package Pokemons;
import ru.ifmo.se.pokemon.*;
import SpecialMoves.*;
import StatusMoves.*;

public class Graveler extends Pokemon {
    public Graveler(String name, int level) {
        super(name, level);
        setType(Type.ROCK, Type.GROUND);
        setStats(4, 6, 7, 3, 3, 3);
        setMove(new LightScreen(), new ThunderShock(), new ThunderWave());
    }
}
