package solver;

public class State
{
    private Pos player;
    private char[][] itemsData;
    private long hash;
    public Pos getPlayer() {
        return player;
    }
    public char[][] getItemsData() {
        return itemsData;
    }
}
