package robot.algorithms;

import robot.client.Robot;

public class AroundRobot {
  private final Robot r;
  
  private enum Side {
    UP, RIGHT, DOWN, LEFT;
  }
  
  private Side side = Side.RIGHT;
  
  public void resetRight() {
    side = Side.RIGHT;
  }
  
  public AroundRobot(Robot r) {
    this.r = r;
  }
  
  public void turnRight() {
    switch (side) {
    case RIGHT:
      side = Side.DOWN;
      return;
    case DOWN:
      side = Side.LEFT;
      return;
    case LEFT:
      side = Side.UP;
      return;
    case UP:
      side = Side.RIGHT;
      return;
    }
  }
  
  public void turnLeft() {
    switch (side) {
    case RIGHT:
      side = Side.UP;
      return;
    case DOWN:
      side = Side.RIGHT;
      return;
    case LEFT:
      side = Side.DOWN;
      return;
    case UP:
      side = Side.LEFT;
      return;
    }
  }
  
  public void step() {
    switch (side) {
    case RIGHT:
      r.right();
      return;
    case DOWN:
      r.down();
      return;
    case LEFT:
      r.left();
      return;
    case UP:
      r.up();
      return;
    }
  }
  
  public boolean hasWall() {
    switch (side) {
    case DOWN:
      return r.isBorderBottom();
    case UP:
      return r.isBorderTop();
    case LEFT:
      return r.isBorderLeft();
    case RIGHT:
      return r.isBorderRight();
      
    default:
      throw new RuntimeException();
    }
  }
  
  public boolean hasWallLeft() {
    turnLeft();
    try {
      return hasWall();
    } finally {
      turnRight();
    }
  }
  
  public boolean hasWallRight() {
    turnRight();
    try {
      return hasWall();
    } finally {
      turnLeft();
    }
  }
  
  public boolean hasWallBack() {
    turnRight();
    turnRight();
    try {
      return hasWall();
    } finally {
      turnRight();
      turnRight();
    }
  }
}
