package Pokemons;
import PhysicalMoves.*;

public class Kadabra extends Abra {
    public Kadabra(String name, int level) {
        super(name, level);
        setStats(3, 3, 2, 8, 5, 7);
        addMove(new TripleKick());
    }
}
