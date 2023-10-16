package solver;

public class State {


    private Pos player;
    private char[][] itemsData;
    private long hash;
    private int heuristic;
    private String path;

    public State(Pos player, char[][] itemsData, long hash, int heuristic, String path)
    {
        this.player = player;
        this.itemsData = itemsData;
        this.hash = hash;
        this.heuristic = heuristic;
        this.path = path;
    }


    public Pos getPlayer()
    {
        return player;
    }
    public void setPlayer(Pos player) {
        this.player = player;
    }
    public char[][] getItemsData()
    {
        return itemsData;
    }

    public long getHash()
    {
        return hash;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public String getPath() {
        return path;
    }
}
