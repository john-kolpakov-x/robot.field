package robot.algorithms;

import robot.client.RemoteRobot;

public class FillRegion {
  public static void main(String[] args) throws InterruptedException {
    Thread.sleep(3000);
    new FillRegion().run();
  }
  
  public FillRegion() {
    r = new RemoteRobot("127.0.0.1", 1921);
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

    System.out.println("Complete");
  }
  
}
