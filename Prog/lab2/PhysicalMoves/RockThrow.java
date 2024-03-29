package PhysicalMoves;
import ru.ifmo.se.pokemon.*;

public class RockThrow extends PhysicalMove {
    public RockThrow() {
        super(Type.ROCK, 50.0, 90.0);
    }

    public String describe() {
        return "using physical attack Rock Throw";
    }

    public double calcBaseDamage(Pokemon attacker, Pokemon defender) {
        double damage = super.calcBaseDamage(attacker, defender);

        if (damage > 0.25f) {
            System.out.println(defender.toString() + ": мне больно (DMG: " + Double.toString(damage) + ")");
        }

        return damage;
    }
}
