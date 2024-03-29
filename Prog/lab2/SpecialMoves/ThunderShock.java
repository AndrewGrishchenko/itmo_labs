package SpecialMoves;
import ru.ifmo.se.pokemon.*;

public class ThunderShock extends SpecialMove{
    public ThunderShock() {
        super(Type.ELECTRIC, 40.0, 100.0);
    }

    public void applyOppEffects(Pokemon p) {
        if (!p.hasType(Type.ELECTRIC) && Math.random() <= 0.1) {
            Effect.paralyze(p);
        }
    }

    public String describe() {
        return "using special attack Thunder Shock";
    }

    public double calcBaseDamage(Pokemon attacker, Pokemon defender) {
        double damage = super.calcBaseDamage(attacker, defender);
        
        if (damage > 0.25f) {
            System.out.println(defender.toString() + ": мне больно (DMG: " + Double.toString(damage) + ")");
        }

        return damage;
    }
}
