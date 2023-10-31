package Pokemons;
import PhysicalMoves.*;

public class Alakazam extends Kadabra {
    public Alakazam(String name, int level) {
        super(name, level);
        setStats(4, 3, 3, 8, 6, 8);
        addMove(new RockThrow());
    }
}