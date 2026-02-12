package mazelet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class Player {
  private int row;
  private int col;
  private int cellSize;
  private int x; // x position
  private int y; // y position
  private int points = 50;


  public Player(int startX, int startY, int size) {
    x = startX;
    y = startY;
    cellSize = size;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public void moveUp() {
    row--;
  }

  public void moveDown() {
    row++;
  }

  public void moveLeft() {
    col--;
  }

  public void moveRight() {
    col++;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  //points

  public void setPoints(int amt) {
    this.points += amt;
  }

  public int getPoints() {
    return points;
  }

  public void update() {
    //extra updates
  }

  public void draw(Graphics g) {
    int centerX = (col * cellSize) + (cellSize / 2);
    int centerY = (row * cellSize) + (cellSize / 2);
    int radius = cellSize / 2;

    g.setColor(Color.BLUE);
    g.fillOval(centerX - radius, centerY - radius, cellSize, cellSize);
  }
}