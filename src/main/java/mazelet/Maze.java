package mazelet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.*;

public class Maze extends JPanel implements KeyListener {
  private final int mazeWidth;
  private final int mazeHeight;
  private final int cellSize;
  private final int[][] mazeGrid;
  private Player player;
  private final int startRow;
  private final int startCol;
  private final int endRow;
  private final int endCol;
  private int score;
  private final List<Point> shortestPath;

  /**
   * Initializes a new Maze object.
   *
   * @param mazeWidth  the width of the maze.
   * @param mazeHeight the height of the maze.
   * @param cellSize   the size of each cell in the maze.
   */
  public Maze(int mazeWidth, int mazeHeight, int cellSize) {
    this.mazeWidth = mazeWidth;
    this.mazeHeight = mazeHeight;
    this.cellSize = cellSize;
    this.mazeGrid = new int[mazeHeight][mazeWidth];
    this.startRow = 0;
    this.startCol = 0;
    this.endRow = mazeHeight - 2;
    this.endCol = mazeWidth - 2;
    this.score = 20;

    generateMaze();

    this.shortestPath = bfs(startRow + 1, startCol + 1, endRow, endCol);
    placeEnemiesAndCoins();

    player = new Player(startCol + 1, startRow + 1, cellSize);

    setPreferredSize(new Dimension(mazeWidth * cellSize, mazeHeight * cellSize));
    setFocusable(true);
    addKeyListener(this);
  }

  /**
   * Generates the maze using the random maze Prim's algorithm.
   */
  private void generateMaze() {
    // maze grid with walls
    for (int row = 0; row < mazeHeight; row++) {
      for (int col = 0; col < mazeWidth; col++) {
        mazeGrid[row][col] = 1;
      }
    }

    // random maze Prim's algorithm
    Random random = new Random();
    int startCellRow = 1;
    int startCellCol = 1;
    mazeGrid[startCellRow][startCellCol] = 0;
    List<Integer> walls = new ArrayList<>();
    walls.add(startCellRow * mazeWidth + startCellCol);

    while (!walls.isEmpty()) {
      int randomWallIndex = random.nextInt(walls.size());
      int currentWall = walls.get(randomWallIndex);
      int row = currentWall / mazeWidth;
      int col = currentWall % mazeWidth;
      walls.remove(randomWallIndex);

      int[] neighbors = new int[] {1, 0, -1, 0, 0, 1, 0, -1};
      for (int i = 0; i < neighbors.length; i += 2) {
        int neighborRow = row + neighbors[i];
        int neighborCol = col + neighbors[i + 1];

        if (neighborRow > 0 && neighborRow < mazeHeight - 1 && neighborCol > 0 &&
            neighborCol < mazeWidth - 1) {
          if (mazeGrid[neighborRow][neighborCol] == 1) {
            int wallCount = countWalls(neighborRow, neighborCol);
            if (wallCount == 3) {
              mazeGrid[neighborRow][neighborCol] = 0;
              walls.add(neighborRow * mazeWidth + neighborCol);
            }
          }
        }
      }
    }

    // Set the finish cell at the bottom right of the maze
    mazeGrid[mazeHeight - 2][mazeWidth - 2] = 2;
    if (mazeGrid[mazeHeight - 3][mazeWidth - 2] == 1 &&
        mazeGrid[mazeHeight - 2][mazeWidth - 3] == 1) {
      mazeGrid[mazeHeight - 3][mazeWidth - 2] = 0;
    }
  }

  /**
   * Counts the number of walls surrounding the specified cell.
   *
   * @param row the row of the cell.
   * @param col the column of the cell.
   * @return the number of walls surrounding the cell.
   */
  private int countWalls(int row, int col) {
    int wallCount = 0;
    int[] neighbors = new int[] {1, 0, -1, 0, 0, 1, 0, -1};
    for (int i = 0; i < neighbors.length; i += 2) {
      int neighborRow = row + neighbors[i];
      int neighborCol = col + neighbors[i + 1];

      if (mazeGrid[neighborRow][neighborCol] == 1) {
        wallCount++;
      }
    }
    return wallCount;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // draw the maze
    for (int row = 0; row < mazeHeight; row++) {
      for (int col = 0; col < mazeWidth; col++) {
        int x = col * cellSize;
        int y = row * cellSize;

        if (mazeGrid[row][col] == 1) {
          g.setColor(Color.BLACK);
          g.fillRect(x, y, cellSize, cellSize);
        } else if (mazeGrid[row][col] == 2) {
          g.setColor(Color.GREEN);
          g.fillRect(x, y, cellSize, cellSize);
        } else if (mazeGrid[row][col] == 3) {
          g.setColor(Color.RED);
          g.fillRect(x, y, cellSize, cellSize);
        } else if (mazeGrid[row][col] == 4) {
          g.setColor(Color.YELLOW);
          g.fillOval(x, y, cellSize, cellSize);
        } else {
          g.setColor(Color.WHITE);
          g.fillRect(x, y, cellSize, cellSize);
        }
      }
    }

    // draw the player
    player.draw(g);

    // display the current score
    g.setColor(Color.WHITE);
    g.drawString("Score: " + score, 10, 20);
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
    //int keyCode = e.getKeyCode();

    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_UP) {
      if ((player.getRow() > 0 && mazeGrid[player.getRow() - 1][player.getCol()] != 1) ||
          (player.getRow() == 0 && player.getCol() == 0)) {
        player.moveUp();
      }
    } else if (keyCode == KeyEvent.VK_DOWN) {
      if ((player.getRow() < mazeHeight - 1 &&
          mazeGrid[player.getRow() + 1][player.getCol()] != 1) ||
          (player.getRow() == 0 && player.getCol() == 0)) {
        player.moveDown();
      }
    } else if (keyCode == KeyEvent.VK_LEFT) {
      if ((player.getCol() > 0 && mazeGrid[player.getRow()][player.getCol() - 1] != 1) ||
          (player.getRow() == 0 && player.getCol() == 0)) {
        player.moveLeft();
      }
    } else if (keyCode == KeyEvent.VK_RIGHT) {
      if ((player.getCol() < mazeWidth - 1 &&
          mazeGrid[player.getRow()][player.getCol() + 1] != 1) ||
          (player.getRow() == 0 && player.getCol() == 0)) {
        player.moveRight();
      }
    }

        /*if (keyCode == KeyEvent.VK_UP) {
            player.moveUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            player.moveDown();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            player.moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            player.moveRight();
        }*/

    // Check if player reached the finish cell
    if (player.getRow() == endRow && player.getCol() == endCol) {
      JOptionPane.showMessageDialog(this, "You Win! Final Score: " + score, "Game Over",
          JOptionPane.PLAIN_MESSAGE);
      System.exit(0);
    }

    // Check if player hits an enemy
    if (mazeGrid[player.getRow()][player.getCol()] == 3) {
      score -= 5;
      mazeGrid[player.getRow()][player.getCol()] = 0; // Remove the enemy from the maze
    }

    // Check if player collects a coin
    if (mazeGrid[player.getRow()][player.getCol()] == 4) {
      score += 3;
      mazeGrid[player.getRow()][player.getCol()] = 0; // Remove the coin from the maze
    }

    repaint();
  }

  /**
   * Checks if the specified cell is free (i.e., not a wall) and within the maze boundaries.
   *
   * @param row the row of the cell.
   * @param col the column of the cell.
   * @return true if the cell is free, false otherwise.
   */
  public boolean isCellFree(int row, int col) {
    if (row >= 0 && row < mazeHeight && col >= 0 && col < mazeWidth) {
      return mazeGrid[row][col] == 0 || mazeGrid[row][col] == 2 || mazeGrid[row][col] == 3 ||
          mazeGrid[row][col] == 4;
    }
    return false;
  }

  /**
   * Places enemies and coins in the maze randomly.
   */
  public void placeEnemiesAndCoins() {
    // Create a set to store the coordinates of the shortest path
    Set<Point> shortestPathSet = new HashSet<>(shortestPath);
      for (Point p : shortestPathSet) {
          System.out.println(p.getX() + " " + p.getY());
      }


    Random random = new Random();
    int enemyCount = 0;
    while (enemyCount < 10) {
      int row = random.nextInt(mazeHeight);
      int col = random.nextInt(mazeWidth);

      // Check if the generated coordinate is a valid empty cell and not in the shortest path
      if (mazeGrid[row][col] == 0 && !shortestPathSet.contains(new Point(row, col))) {
        mazeGrid[row][col] = 3; // Place an enemy
        enemyCount++;
      }
    }

    int coinCount = 0;
    while (coinCount < 10) {
      int row = random.nextInt(mazeHeight);
      int col = random.nextInt(mazeWidth);

      // Check if the generated coordinate is a valid empty cell and not in the shortest path
      if (mazeGrid[row][col] == 0 && !shortestPathSet.contains(new Point(row, col))) {
        mazeGrid[row][col] = 4; // Place a coin
        coinCount++;
      }
    }
  }

  /**
   * Performs a breadth-first search to find the shortest path from the start cell to the end cell in the maze.
   *
   * @param startRow the row of the start cell.
   * @param startCol the column of the start cell.
   * @param endRow   the row of the end cell.
   * @param endCol   the column of the end cell.
   * @return a list of points representing the shortest path.
   */
  public List<Point> bfs(int startRow, int startCol, int endRow, int endCol) {
    boolean[][] visited = new boolean[mazeHeight][mazeWidth];
    Queue<List<Point>> queue = new LinkedList<>();

    queue.add(new ArrayList<>(Collections.singletonList(new Point(startRow, startCol))));
    visited[startRow][startCol] = true;

    int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    while (!queue.isEmpty()) {
      List<Point> path = queue.poll();
      Point lastPoint = path.get(path.size() - 1);
      int curRow = lastPoint.x;
      int curCol = lastPoint.y;

      if (curRow == endRow && curCol == endCol) {
          for (Point p : path) {
              System.out.println(p.getX() + " " + p.getY());
          }
        return path; // Reached the end cell, return the shortest path
      }

      for (int[] dir : dirs) {
        int newRow = curRow + dir[0];
        int newCol = curCol + dir[1];


        if (newRow >= 0 && newRow < mazeHeight && newCol >= 0 && newCol < mazeWidth
            && mazeGrid[newRow][newCol] != 1 && !visited[newRow][newCol]) {

          List<Point> newPath = new ArrayList<>(path);
          newPath.add(new Point(newRow, newCol));
          queue.add(newPath);
          visited[newRow][newCol] = true;
        }
      }
    }

    return null; // No path found, return null
  }


  public static void main(String[] args) {
    int mazeWidth = 25;
    int mazeHeight = 25;
    int cellSize = 25;

    JFrame frame = new JFrame("Maze Game");
    Maze maze = new Maze(mazeWidth, mazeHeight, cellSize);
    frame.add(maze);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}




