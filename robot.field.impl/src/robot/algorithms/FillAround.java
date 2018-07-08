package robot.algorithms;

import java.util.HashSet;
import java.util.Set;

import robot.client.RemoteRobot;

public class FillAround {
  public static void main(String[] args) throws InterruptedException {
    Thread.sleep(3000);
    new FillAround().run();
    System.out.println("COMPLETE");
  }
  
  static class Point {
    final int x, y;
    
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + x;
      result = prime * result + y;
      return result;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Point other = (Point) obj;
      if (x != other.x) return false;
      if (y != other.y) return false;
      return true;
    }
  }
  
  private final Set<Point> paintedSet = new HashSet<>();
  private int x, y;
  
  private boolean painted() {
    return paintedSet.contains(new Point(x, y));
  }
  
  public FillAround() {
    r = new RemoteRobot("127.0.0.1", 1921);
  }
  
  final RemoteRobot r;
  
  private void run() {
    fillRegion();
  }
  
  private void fillRegion() {
    if (painted()) return;
    paint();
    if (r.painted()) return;
    r.paint();
    
    if (!painted(x, y - 1) && !r.isBorderTop()) {
      r.up();
      y--;
      fillRegion();
      r.down();
      y++;
    }
    
    if (!painted(x + 1, y) && !r.isBorderRight()) {
      r.right();
      x++;
      fillRegion();
      r.left();
      x--;
    }
    
    if (!painted(x, y + 1) && !r.isBorderBottom()) {
      r.down();
      y++;
      fillRegion();
      r.up();
      y--;
    }
    
    if (!painted(x - 1, y) && !r.isBorderLeft()) {
      r.left();
      x--;
      fillRegion();
      r.right();
      x++;
    }
  }
  
  private boolean painted(int xx, int yy) {
    return paintedSet.contains(new Point(xx, yy));
  }
  
  private void paint() {
    paintedSet.add(new Point(x, y));
  }
  
}
