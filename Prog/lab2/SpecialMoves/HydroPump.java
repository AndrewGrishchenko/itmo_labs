package SpecialMoves;
import ru.ifmo.se.pokemon.*;

public class HydroPump extends SpecialMove {
    public HydroPump() {
        super(Type.WATER, 110.0, 80.0);
    }

    public String describe() {
        return "using special attack Hydro Pump";
    }

    public double calcBaseDamage(Pokemon attacker, Pokemon defender) {
        double damage = super.calcBaseDamage(attacker, defender);
        
        if (damage > 0.25f) {
            System.out.println(defender.toString() + ": мне больно (DMG: " + Double.toString(damage) + ")");
        }

        return damage;
    }
}
