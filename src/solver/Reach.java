package solver;

import java.util.ArrayList;

// tiles will be accessed using column-major ordering
public record Reach(Pos min, int stamp, ArrayList<ReachPos> tiles) {
}
