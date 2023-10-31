package StatusMoves;
import ru.ifmo.se.pokemon.*;

public class ThunderWave extends StatusMove {
    public ThunderWave() {
        super(Type.ELECTRIC, 0.0, 90.0);
    }

    public void applyOppEffects(Pokemon p) {
        if (!p.hasType(Type.ELECTRIC)) {
            Effect.paralyze(p);
        }
    }

    public String describe() {
        return "using status attack Thunder Wave";
    }
}
