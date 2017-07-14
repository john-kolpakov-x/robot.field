package robot.algorithms;

import robot.client.RemoteRobot;

public class GoCherezKolidor {
  public static void main(String[] args) {
    new GoCherezKolidor().run();
  }
  
  private final AroundRobot r;
  
  public GoCherezKolidor() {
    r = new AroundRobot(new RemoteRobot("127.0.0.1", 1921));
  }
  
  private void run() {
    r.resetRight();
    r.step();
    
    W: while (inKolidor()) {
      if (!r.hasWall()) {
        r.step();
        continue W;
      }
      if (!r.hasWallLeft()) {
        r.turnLeft();
        continue W;
      }
      if (!r.hasWallRight()) {
        r.turnRight();
        continue W;
      }
    }
    
    System.out.println("COMPLETE");
  }
  
  private boolean inKolidor() {
    return r.hasWall() || r.hasWallLeft() || r.hasWallRight();
  }
}
