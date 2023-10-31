package PhysicalMoves;
import ru.ifmo.se.pokemon.*;

public class RockThrow extends PhysicalMove {
    public RockThrow() {
        super(Type.ROCK, 50.0, 90.0);
    }

    public String describe() {
        return "using physical attack Rock Throw";
    }
}
