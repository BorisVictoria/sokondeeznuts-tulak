package solver;

import java.util.*;

public class SokoBot
{
  private final int width;
  private final int height;
  private final char[][] mapData;
  private final char[][] itemsData;
  private Reach reach;
  private Pos player;
  private State current;
  private final ArrayList<Pos> goals;
  private final long[][][] zobristTable;

  //private final Comparator<Pos> posComparator;

  public SokoBot(int width, int height, char[][] mapData, char[][] itemsData)
  {
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;
    this.zobristTable = new long[2][height][width];
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
    this.reach = new Reach(height, width);
    clear();
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
    char[][] itemsData = toCheck.getItemsData();
    for (int i = 0; i < goals.size(); i++)
    {
      if (itemsData[goals.get(i).y()][goals.get(i).x()] != '$')
      {
        s = false;
        break;
      }
    }

    return s;

  }
  public void clear()
  {
    reach.setMin(new Pos(0,0));
    reach.setStamp(0);
    int[][] tiles = reach.getTiles();

    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        if (mapData[i][j] == '#')
          tiles[i][j] = Integer.MAX_VALUE;
        else
          tiles[i][j] = 0;
      }
    }
  }
  public void calculateReach(Pos start, char[][] itemsData)
  {
    //Reset before overflow
    if (reach.getStamp() >= Integer.MAX_VALUE - 10)
      clear();

    //Initialization
    reach.setStamp(reach.getStamp()+2);
    reach.setMin(start);
    int[][] tiles = reach.getTiles();
    int stamp = reach.getStamp();
    tiles[start.y()][start.x()] = stamp;

    Queue<Pos> queue = new ArrayDeque<Pos>();
    queue.add(start);

    while (!queue.isEmpty())
    {

      Pos toCheck = queue.poll();

      if (tiles[toCheck.y()-1][toCheck.x()] < stamp)
      {
        if (itemsData[toCheck.y()-1][toCheck.x()] == '$')
        {
          tiles[toCheck.y()-1][toCheck.x()] = stamp + 1;
        }
        else
        {
          Pos up = new Pos(toCheck.x(), toCheck.y()-1);
          queue.add(up);
          tiles[toCheck.y()-1][toCheck.x()] = stamp;
          if ((reach.getMin().x() * width + reach.getMin().y()) - (up.x() * width + up.y()) > 0)
            reach.setMin(up);
        }
      }

      if (tiles[toCheck.y()+1][toCheck.x()] < stamp)
      {
        if (itemsData[toCheck.y()+1][toCheck.x()] == '$')
        {
          tiles[toCheck.y()+1][toCheck.x()] = stamp + 1;
        }
        else
        {
          Pos down = new Pos(toCheck.x(), toCheck.y()+1);
          queue.add(down);
          tiles[toCheck.y()+1][toCheck.x()] = stamp;
          if ((reach.getMin().x() * width + reach.getMin().y()) - (down.x() * width + down.y()) > 0)
            reach.setMin(down);
        }
      }

      if (tiles[toCheck.y()][toCheck.x()+1] < stamp)
      {
        if (itemsData[toCheck.y()][toCheck.x()+1] == '$')
        {
          tiles[toCheck.y()][toCheck.x()+1] = stamp + 1;
        }
        else
        {
          Pos right = new Pos(toCheck.x()+1, toCheck.y());
          queue.add(right);
          tiles[toCheck.y()][toCheck.x()+1] = stamp;
          if ((reach.getMin().x() * width + reach.getMin().y()) - (right.x() * width + right.y()) > 0)
            reach.setMin(right);
        }
      }

      if (tiles[toCheck.y()][toCheck.x()-1] < stamp)
      {
        if (itemsData[toCheck.y()][toCheck.x()-1] == '$')
        {
          tiles[toCheck.y()][toCheck.x()-1] = stamp + 1;

        }
        else
        {
          Pos left = new Pos(toCheck.x()-1, toCheck.y());
          queue.add(left);
          tiles[toCheck.y()][toCheck.x()-1] = stamp;
          if ((reach.getMin().x() * width + reach.getMin().y()) - (left.x() * width + left.y()) > 0)
            reach.setMin(left);
        }
      }

    }

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
        else if (itemsData[i][j] == '@')
        {
          key ^= zobristTable[1][i][j];
        }
      }
    }
    return key;
  }

  public int calculateHeuristic(char[][] itemsData)
  {
    int heuristic = 0;
    int min;
    for(int i = 0; i < height; i++)
    {
      for(int j = 0; j < width; j++)
      {
        if(itemsData[i][j] == '$')
        {
          min = 999;
          for(int k = 0; k < goals.size(); k++)
          {
            int dist = Math.abs(j - goals.get(k).x()) + Math.abs(i - goals.get(k).y());
            if(dist < min)
              min = dist;
          }
          heuristic += min;
        }
      }
    }
    return heuristic;
  }

  public String calculatePath(Pos player, Pos dest)
  {

    Comparator<Path> posComparator = Comparator.comparing(Path::heuristic);
    PriorityQueue<Path> queue = new PriorityQueue<>(posComparator);

    int heuristic = Math.abs(player.x() - dest.x()) + Math.abs(player.y() - dest.y());
    Path start = new Path(player, heuristic, "");
    HashSet<Pos> visited = new HashSet<>();
    int[][] tiles = reach.getTiles();
    int stamp = reach.getStamp();

    queue.offer(start);

    while (!queue.isEmpty())
    {
      Path current = queue.poll();
      //System.out.println(current.path());

      if (current.pos().x() == dest.x() && current.pos().y() == dest.y())
      {
        return current.path();
      }

      Pos curPos = current.pos();

      if (tiles[curPos.y()-1][curPos.x()] == stamp)
      {
         Pos posup = new Pos(curPos.x(),curPos.y()-1);
         int h = Math.abs(posup.x() - dest.x()) + Math.abs(posup.y() - dest.y());
         Path up = new Path(posup, h, current.path() + "u");
         if (!visited.contains(posup))
          queue.add(up);
      }

      if (tiles[curPos.y()+1][curPos.x()] == stamp)
      {
        Pos posdown = new Pos(curPos.x(),curPos.y()+1);
        int h = Math.abs(posdown.x() - dest.x()) + Math.abs(posdown.y() - dest.y());
        Path down = new Path(posdown, h, current.path() + "d");
        if (!visited.contains(posdown))
          queue.add(down);
      }

      if (tiles[curPos.y()][curPos.x()+1] == stamp)
      {
        Pos posright = new Pos(curPos.x()+1,curPos.y());
        int h = Math.abs(posright.x() - dest.x()) + Math.abs(posright.y() - dest.y());
        Path right = new Path(posright, h, current.path() + "r");
        if (!visited.contains(posright))
          queue.add(right);
      }

      if (tiles[curPos.y()][curPos.x()-1] == stamp)
      {
        Pos posleft = new Pos(curPos.x()-1,curPos.y());
        int h = Math.abs(posleft.x() - dest.x()) + Math.abs(posleft.y() - dest.y());
        Path left = new Path(posleft, h, current.path() + "l");
        if (!visited.contains(posleft))
          queue.add(left);
      }

      visited.add(curPos);
    }

    throw new RuntimeException("Wasn't able to find the path!");

  }
  public String solveSokobanPuzzle()
  {

    Comparator<State> comp = Comparator.comparing(State::getHeuristic);
    PriorityQueue<State> states = new PriorityQueue<State>(comp);
    State start = new State(player, itemsData, calculateHash(itemsData), calculateHeuristic(itemsData),"");
    HashSet<Long> visited = new HashSet<Long>();
    states.offer(start);

//    calculateReach(player, itemsData);
//        for (int i = 0; i < height; i++)
//        {
//          for (int j = 0; j < width; j++)
//          {
//            if (reach.getTiles()[i][j] == Integer.MAX_VALUE)
//              System.out.print("+");
//            else
//              System.out.print(reach.getTiles()[i][j]);
//          }
//          System.out.println();
//        }
//
//    return calculatePath(player, new Pos(1, 1));

    int nodes = 0;
    while (!states.isEmpty())
    {

        current = states.poll();
        char[][] curItemsData = current.getItemsData();
        Pos curPlayer = current.getPlayer();
        calculateReach(curPlayer, curItemsData);

//        for (int i = 0; i < height; i++)
//        {
//          for (int j = 0; j < width; j++)
//          {
//            if (curItemsData[i][j] == ' ')
//              System.out.print(mapData[i][j]);
//            else System.out.print(curItemsData[i][j]);
//          }
//          System.out.println();
//        }

        if (isSolved(current))
        {
          System.out.println("Nodes cummed on: " + nodes);
          System.out.println("We are done!");
          return current.getPath();
        }
        else
        {
          for (int i = 0; i < height; i++)
          {
            for (int j = 0; j < width; j++)
            {
              if (curItemsData[i][j] == '$') //check if box
              {
                if (reach.getTiles()[i][j] == reach.getStamp() + 1) //check if box is reachable
                {

                  //.
                  //$
                  //@ go up
                  //System.out.println("Checking up!");
                  if(reach.getTiles()[i+1][j] == reach.getStamp() && mapData[i-1][j] != '#' && curItemsData[i-1][j] != '$' && !deadTiles[i-1][j])
                  {
                    //System.out.println("Up valid!");
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@'; //replace with player
                    newItemsData[i - 1][j] = '$'; //move box

                    if (isSolvable(newItemsData, new Pos(j, i-1)))
                    {
                      long newHash = calculateHash(newItemsData);
                      if (!visited.contains(newHash))
                      {
                        State up = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), current.getPath() + calculatePath(curPlayer, new Pos(j,i+1)) + "u");
                        states.offer(up);
                      }
                    else
                    {
                      System.out.println("Up already visited!");
                    }

                    }
                  }


                  //@
                  //$
                  //. go down
                  //System.out.println("Checking down!");
                  if(reach.getTiles()[i-1][j] == reach.getStamp() && mapData[i+1][j] != '#' && curItemsData[i+1][j] != '$' && !deadTiles[i+1][j])
                  {
                    //System.out.println("Down valid!");
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i+1][j] = '$';

                    if (isSolvable(newItemsData, new Pos(j, i+1)))
                    {
                      long newHash = calculateHash(newItemsData);
                      if (!visited.contains(newHash))
                      {
                        State down = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), current.getPath() + calculatePath(curPlayer, new Pos(j,i-1)) + "d");
                        states.offer(down);
                      }
                    else
                    {
                      System.out.println("Down already visited!");
                    }
                    }

                  }

                  //@$. go right
                  //System.out.println("Checking right!");
                  if(reach.getTiles()[i][j-1] == reach.getStamp() && mapData[i][j+1] != '#' && curItemsData[i][j+1] != '$' && !deadTiles[i][j+1])
                  {
                    //System.out.println("Right valid!");
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i][j+1] = '$';
                    
                    if (isSolvable(newItemsData, new Pos(j+1, i)))
                    {
                      long newHash = calculateHash(newItemsData);
                      if (!visited.contains(newHash))
                      {
                        State right = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), current.getPath() + calculatePath(curPlayer, new Pos(j-1,i)) + "r");
                        states.offer(right);
                      }
                      else
                      {
                       System.out.println("Right already visited!");
                      }
                    }


                  }

                  //.$@ go left
                  ////System.out.println("Checking left!");
                  if(reach.getTiles()[i][j+1] == reach.getStamp() && mapData[i][j-1] != '#' && curItemsData[i][j-1] != '$' && !deadTiles[i][j-1])
                  {
                    //System.out.println("Left valid!");
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i][j-1] = '$';

                    if (isSolvable(newItemsData, new Pos(j-1, i)))
                    {
                      long newHash = calculateHash(newItemsData);
                      if (!visited.contains(newHash))
                      {
                        State left = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), current.getPath() + calculatePath(curPlayer, new Pos(j+1, i)) + "l");
                        states.offer(left);
                      }
                    else
                    {
                      System.out.println("Left already visited!");
                    }
                    }

                  }
                }
              }
            }
          }

          visited.add(current.getHash());
        }
      nodes++;
    }

    System.out.println("We are not done!");

    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

}
