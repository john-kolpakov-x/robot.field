package robot.algorithms;

import robot.client.RemoteRobot;

public class Probe {
  public static void main(String[] args) {
    new Probe().run();
    System.out.println("COMPLETE");
  }

  public Probe() {
    r = new RemoteRobot("127.0.0.1", 1921);
  }

  final RemoteRobot r;

  private void run() {
    System.out.println(r.painted());
    r.paint();
    System.out.println(r.painted());
  }

}
