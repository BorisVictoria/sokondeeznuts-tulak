package solver;

public class SokoBot
{

  private int width;
  private int height;
  private char[][] mapData;
  private char[][] itemsData;

  public SokoBot(int width, int height, char[][] mapData, char[][] itemsData)
  {
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;

  }

  public String solveSokobanPuzzle()
  {

    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

}
