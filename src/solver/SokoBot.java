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
    if (reach.getStamp() >= Integer.MAX_VALUE - 2)
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
    PriorityQueue<Path> queue = new PriorityQueue<Path>(posComparator);
    String path = "";

    int heuristic = Math.abs(player.x() - dest.x()) + Math.abs(player.y() - dest.y());
    Path start = new Path(player, heuristic, path);
    int[][] tiles = reach.getTiles();
    int stamp = reach.getStamp();

    queue.offer(start);

    while (!queue.isEmpty())
    {
      Path current = queue.poll();
      if (current.heuristic() == 0)
      {
        return current.path();
      }

      Pos curPos = current.pos();

      if (tiles[curPos.y()-1][curPos.x()] == stamp)
      {
         Pos up = new Pos(curPos.x(),curPos.y()-1);
         int h = Math.abs(up.x() - dest.x()) + Math.abs(up.y() - dest.y());
         queue.add(new Path(up, h, current.path() + "u"));
      }

      if (tiles[curPos.y()+1][curPos.x()] == stamp)
      {
        Pos down = new Pos(curPos.x(),curPos.y()+1);
        int h = Math.abs(down.x() - dest.x()) + Math.abs(down.y() - dest.y());
        queue.add(new Path(down, h, current.path() + "d"));
      }

      if (tiles[curPos.y()][curPos.x()+1] == stamp)
      {
        Pos right = new Pos(curPos.x()+1,curPos.y());
        int h = Math.abs(right.x() - dest.x()) + Math.abs(right.y() - dest.y());
        queue.add(new Path(right, h, current.path() + "r"));
      }

      if (tiles[curPos.y()][curPos.x()-1] == stamp)
      {
        Pos left = new Pos(curPos.x()-1,curPos.y());
        int h = Math.abs(left.x() - dest.x()) + Math.abs(left.y() - dest.y());
        queue.add(new Path(left, h, current.path() + "l"));
      }
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

    while (!states.isEmpty())
    {

        current = states.poll();
        char[][] curItemsData = current.getItemsData();
        Pos curPlayer = current.getPlayer();
        calculateReach(curPlayer, curItemsData);

        if (isSolved(current))
        {
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
                  if(reach.getTiles()[i+1][j] == reach.getStamp() && curItemsData[i-1][j] == ' ')
                  {
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@'; //replace with player
                    newItemsData[i - 1][j] = '$'; //move box

                    long newHash = calculateHash(newItemsData);
                    if (!visited.contains(newHash))
                    {
                      State up = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), calculatePath(curPlayer, new Pos(j,i+1)) + "u");
                      states.add(up);
                    }
                    else
                    {
                      System.out.println("State already visited!");
                    }

                  }

                  //@
                  //$
                  //. go down
                  if(reach.getTiles()[i-1][j] == reach.getStamp() && curItemsData[i+1][j] == ' ')
                  {
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i+1][j] = '$';

                    long newHash = calculateHash(newItemsData);
                    if (!visited.contains(newHash))
                    {
                      State down = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), calculatePath(curPlayer, new Pos(j,i-1)) + "d");
                      states.add(down);
                    }
                    else
                    {
                      System.out.println("State already visited!");
                    }

                  }

                  //@$. go right
                  if(reach.getTiles()[i][j-1] == reach.getStamp() && curItemsData[i][j+1] == ' ')
                  {
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i][j+1] = '$';

                    long newHash = calculateHash(newItemsData);
                    if (!visited.contains(newHash))
                    {
                      State right = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), calculatePath(curPlayer, new Pos(j-1,i)) + "r");
                      states.add(right);
                    }
                    else
                    {
                      System.out.println("State already visited!");
                    }


                  }

                  //.$@ go left
                  if(reach.getTiles()[i][j+1] == reach.getStamp() && curItemsData[i][j-1] == ' ')
                  {
                    char[][] newItemsData = Arrays.stream(curItemsData).map(char[]::clone).toArray(char[][]::new); //copy current items data
                    newItemsData[curPlayer.y()][curPlayer.x()] = ' '; //clear player
                    newItemsData[i][j] = '@';
                    newItemsData[i][j-1] = '$';

                    long newHash = calculateHash(newItemsData);
                    if (!visited.contains(newHash))
                    {
                      State left = new State(new Pos(j,i),newItemsData, calculateHash(newItemsData), calculateHeuristic(newItemsData), calculatePath(curPlayer, new Pos(j+1, i)) + "l");
                      states.add(left);
                    }
                    else
                    {
                      System.out.println("State already visited!");
                    }

                  }
                }
              }
            }
          }
          visited.add(current.getHash());
        }
    }




    return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  }

}
