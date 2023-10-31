package StatusMoves;
import ru.ifmo.se.pokemon.*;

public class Growl extends StatusMove {
    public Growl() {
        super(Type.NORMAL, 0.0, 100.0);
    }

    public void applyOppEffects(Pokemon p) {
        p.setMod(Stat.ATTACK, -1);
    }

    public String describe() {
        return "using status attack Growl";
    }
}
