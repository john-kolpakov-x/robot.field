package robot.algorithms;

import robot.client.RemoteRobot;

public class CalcArea {
  public static void main(String[] args) throws Exception {
    new CalcArea().run();
  }

  public CalcArea() {
    r = new RemoteRobot("127.0.0.1", 1921);
  }
  
  final RemoteRobot r;
  
  private void run()throws Exception {

    Thread.sleep(4000);
    
    r.down();

    
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
