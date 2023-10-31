package Pokemons;
import ru.ifmo.se.pokemon.*;
import PhysicalMoves.*;
import SpecialMoves.*;

public class Abra extends Pokemon{
    public Abra(String name, int level) {
        super(name, level);
        setType(Type.PSYCHIC);
        setStats(2, 2, 1, 7, 4, 6);
        setMove(new Flamethrower(), new ShadowPunch());
    }
}
