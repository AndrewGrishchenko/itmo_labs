import ru.ifmo.se.pokemon.*;
import Pokemons.*;

public class lab2 {
    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon ducklett = new Ducklett("Ducklett1", 1);
        Pokemon graveler = new Graveler("Graveler1", 1);
        Pokemon wooper = new Wooper("Wooper1", 1);

        Pokemon abra = new Abra("Abra2", 1);
        Pokemon kadabra = new Kadabra("Kadabra2", 1);
        Pokemon alakazam = new Alakazam("Alakazam2", 1);
        
        b.addAlly(ducklett);
        b.addAlly(graveler);
        b.addAlly(wooper);

        b.addFoe(abra);
        b.addFoe(kadabra);
        b.addFoe(alakazam);
        b.go();
    }
}