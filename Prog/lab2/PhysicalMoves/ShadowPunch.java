package PhysicalMoves;
import ru.ifmo.se.pokemon.*;

public class ShadowPunch extends PhysicalMove {
    public ShadowPunch() {
        super(Type.GHOST, 60.0, 0.0);
    }

    public boolean checkAccuracy(Pokemon att, Pokemon def) {
        return true;
    }

    public String describe() {
        return "using physical attack Shadow Punch";
    }

    public double calcBaseDamage(Pokemon attacker, Pokemon defender) {
        double damage = super.calcBaseDamage(attacker, defender);

        if (damage > 0.25f) {
            System.out.println(defender.toString() + ": мне больно (DMG: " + Double.toString(damage) + ")");
        }

        return damage;
    }
}
