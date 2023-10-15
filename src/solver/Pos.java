package solver;

import java.util.Objects;

public record Pos(int x, int y)
{
    @Override
    public boolean equals(Object obj) {
        Pos pos2 = (Pos) obj;
        return x == pos2.x() && y == pos2.y();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
