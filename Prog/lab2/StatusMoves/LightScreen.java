package StatusMoves;
import ru.ifmo.se.pokemon.*;

public class LightScreen extends StatusMove {
    public LightScreen() {
        super(Type.PSYCHIC, 0.0, 0.0);
    }

    public void applySelfEffects(Pokemon p) {
        p.setMod(Stat.HP, 1);
    }

    public boolean checkAccuracy(Pokemon att, Pokemon def) {
        return true;
    }

    public String describe() {
        return "using status attack Light Screen";
    }
}
