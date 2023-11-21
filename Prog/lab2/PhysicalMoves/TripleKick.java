package PhysicalMoves;
import ru.ifmo.se.pokemon.*;

public class TripleKick extends PhysicalMove {
    public TripleKick() {
        super(Type.FIGHTING, 10.0, 90.0);
    }

    public String describe() {
        return "using physical attack Triple Kick";
    }

    public double calcBaseDamage(Pokemon attacker, Pokemon defender) {
        double damage = super.calcBaseDamage(attacker, defender);
        this.power = 20;
        damage += super.calcBaseDamage(attacker, defender);
        this.power = 30;
        damage += super.calcBaseDamage(attacker, defender);

        if (damage > 0.25f) {
            System.out.println(defender.toString() + ": мне больно (DMG: " + Double.toString(damage) + ")");
        }

        return damage;
    }
}
