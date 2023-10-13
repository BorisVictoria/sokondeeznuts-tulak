package solver;

// tiles will be accessed using column-major ordering
public record Reach(Pos min, int stamp, Tile[][] tiles) {
}
