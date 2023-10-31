package SpecialMoves;
import ru.ifmo.se.pokemon.*;

public class HydroPump extends SpecialMove {
    public HydroPump() {
        super(Type.WATER, 110.0, 80.0);
    }

    public String describe() {
        return "using special attack Hydro Pump";
    }
}
