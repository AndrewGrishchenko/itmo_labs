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
}
