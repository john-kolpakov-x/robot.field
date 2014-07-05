package robot.algorithms;

import robot.client.RemoteRobot;

public class FillRegion {
  public static void main(String[] args) {
    new FillRegion().run();
  }
  
  public FillRegion() {
    r = new RemoteRobot("127.0.0.1", 8080);
  }
  
  final RemoteRobot r;
  
  private void run() {
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 10; j++) {
        r.paint();
        r.right();
      }
      for (int j = 0; j < 10l; j++) {
        r.left();
      }
      r.down();
    }
  }
  
}
