package robot.algorithms;

import robot.client.RemoteRobot;

public class PaintMore3 {
  public static void main(String[] args) {
    new PaintMore3().run();
  }
  
  public PaintMore3() {
    r = new RemoteRobot("127.0.0.1", 8080);
  }
  
  final RemoteRobot r;
  
  private void run() {
    
    r.right();
    
    while (r.isBorderBottom()) {
      if (more3()) {
        r.paint();
      }
      r.right();
    }
    
  }
  
  private boolean more3() {
    int count = 0;
    while (count < 3 && !r.isBorderTop()) {
      r.up();
      count++;
    }
    for (int i = 0; i < count; i++) {
      r.down();
    }
    return count >= 3;
  }
}
