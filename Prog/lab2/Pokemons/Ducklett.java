package Pokemons;
import ru.ifmo.se.pokemon.*;
import PhysicalMoves.*;
import SpecialMoves.*;
import StatusMoves.*;

public class Ducklett extends Pokemon {
    public Ducklett(String name, int level) {
        super(name, level);
        setType(Type.WATER, Type.FLYING);
        setStats(4, 3, 3, 3, 3, 4);
        setMove(new Growl(), new ThunderShock(), new HydroPump(), new ShadowPunch());
    }
}
