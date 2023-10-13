package solver;

import java.util.ArrayList;
import java.util.Random;

public class SokoBot
{

  private final int width;
  private final int height;
  private final char[][] mapData;
  private final char[][] itemsData;
  private Reach reach;
  private Pos player;

  private final ArrayList<Pos> goals;

  private final long[][][] zobristTable;

  public SokoBot(int width, int height, char[][] mapData, char[][] itemsData)
  {
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;
    this.zobristTable = new long[1][height][width];
    this.goals = new ArrayList<Pos>();

    //Determine position of player
    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        if (itemsData[i][j] == '@')
        {
            this.player = new Pos(j,i);
        }
      }
    }

    //Initialize reachable tiles
    this.reach = new Reach(player, 0, new ArrayList<ReachPos>());

    //Add goal positions
    for(int i = 0; i < height; i++)
    {
      for(int j = 0; j < width; j++)
      {
        if(mapData[i][j] == '.')
        {
          goals.add(new Pos(j, i));
        }
      }
    }

    //Create zobrist table
    //Assume that it's always unique!
    Random rand = new Random();
    for (int i = 0; i < 2; i++)
    {
      for (int j = 0; j < height; j++)
      {
        for (int k = 0; k < width; k++)
        {
          zobristTable[i][j][k] = rand.nextLong();
        }
      }
    }

  }

  public boolean isSolved(State toCheck)
  {
    boolean s = true;
    for (int i = 0; i < goals.size(); i++)
    {
      if (toCheck.getItemsData()[goals.get(i).y()][goals.get(i).x()] != '$')
      {
        s = false;
        break;
      }
    }

    return s;

  }

  public void calculateReach()
  {

  }

  //Calculates the hash of the current state;
  public long calculateHash(char[][] itemsData)
  {
    long key = 0;

    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        if (itemsData[i][j] == '$')
        {
          key ^= zobristTable[0][i][j];
        }
      }
    }
    return key;
  }

  // true if crate, false if player // 0 goUp, 1 goDown, 2 goRight, 3 goLeft
//  public long updateHash(long key, boolean isCrateMoved, int[] pos, int dir)
//  {
//    if (isCrateMoved)
//    {
//      //add player to new position
//      key ^= zobristTable[0][pos[1]][pos[0]];
//
//      //remove crate in new position
//      key ^= zobristTable[1][pos[1]][pos[0]];
//
//      if (dir == 0)
//      {
//        //remove player from previous position
//        key ^= zobristTable[0][pos[1]+1][pos[0]];
//
//        //add crate to new position;
//        key ^= zobristTable[1][pos[1]-1][pos[0]];
//
//      }
//      else if (dir == 1)
//      {
//        //remove player from previous position
//        key ^= zobristTable[0][pos[1]-1][pos[0]];
//
//        //add crate to new position;
//        key ^= zobristTable[1][pos[1]+1][pos[0]];
//      }
//      else if (dir == 2)
//      {
//        //remove player from previous position
//        key ^= zobristTable[0][pos[1]][pos[0]-1];
//
//        //add crate to new position;
//        key ^= zobristTable[1][pos[1]][pos[0]+1];
//      }
//      else if (dir == 3)
//      {
//        //remove player from previous position
//        key ^= zobristTable[0][pos[1]][pos[0]+1];
//
//        //add crate to new position;
//        key ^= zobristTable[1][pos[1]][pos[0]-1];
//      }
//
//    }
//    else
//    {
//      //add player to new position
//      key ^= zobristTable[0][pos[1]][pos[0]];
//
//      //remove player from previous position
//      if (dir == 0)
//      {
//        key ^= zobristTable[0][pos[1]+1][pos[0]];
//      }
//      else if (dir == 1)
//      {
//        key ^= zobristTable[0][pos[1]-1][pos[0]];
//      }
//      else if (dir == 2)
//      {
//        key ^= zobristTable[0][pos[1]][pos[0]-1];
//      }
//      else if (dir == 3)
//      {
//        key ^= zobristTable[0][pos[1]][pos[0]+1];
//      }
//
//    }
//
//    return key;
//
//  }



  public String solveSokobanPuzzle()
  {

    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

}
