package SpecialMoves;
import ru.ifmo.se.pokemon.*;

public class Flamethrower extends SpecialMove {
    public Flamethrower() {
        super(Type.FIRE, 90.0, 100.0);
    }

    public void applyOppEffects(Pokemon p) {
        if (!p.hasType(Type.FIRE) && Math.random() <= 0.1) {
            Effect.burn(p);
        }
    }

    public String describe() {
        return "using special attack Flamethrower";
    }
}
