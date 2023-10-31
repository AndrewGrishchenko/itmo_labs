package SpecialMoves;
import ru.ifmo.se.pokemon.*;

public class Thunderbolt extends SpecialMove {
    public Thunderbolt() {
        super(Type.ELECTRIC, 90.0, 100.0);
    }

    public void applyOppEffects(Pokemon p) {
        if (!p.hasType(Type.ELECTRIC) && Math.random() <= 0.1) {
            Effect.paralyze(p);
        }
    }

    public String describe() {
        return "using special attack Thunderbolt";
    }
}
