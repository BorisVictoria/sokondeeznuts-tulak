package solver;

public class State
{
    private Pos player;
    private Pos[] boxes;

    private char[][] itemsData;

    private long hash;

    public Pos getPlayer() {
        return player;
    }

    public Pos[] getBoxes() {
        return boxes;
    }

    public char[][] getItemsData() {
        return itemsData;
    }
}
