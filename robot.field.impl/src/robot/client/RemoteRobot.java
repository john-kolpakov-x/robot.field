package robot.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import robot.field.Boom;

public class RemoteRobot implements Robot {
  
  private final int port;
  private final String host;
  
  public RemoteRobot(String host, int port) {
    this.host = host;
    this.port = port;
  }
  
  private String request(String in) {
    try {
      Socket s = new Socket(host, port);
      
      try {
        PrintStream out = new PrintStream(s.getOutputStream(), false, "UTF-8");
        out.println(in);
        out.flush();
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
        return rd.readLine();
      } finally {
        s.close();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void up() {
    check(request("go up"));
  }
  
  private String check(String response) {
    if ("ERROR".equals(response)) throw new Boom();
    return response;
  }
  
  @Override
  public void right() {
    check(request("go right"));
  }
  
  @Override
  public void down() {
    check(request("go down"));
  }
  
  @Override
  public void left() {
    check(request("go left"));
  }
  
  @Override
  public boolean isBorderTop() {
    return "YES".equals(check(request("is border top")));
  }
  
  @Override
  public boolean isBorderRight() {
    return "YES".equals(check(request("is border right")));
  }
  
  @Override
  public boolean isBorderBottom() {
    return "YES".equals(check(request("is border bottom")));
  }
  
  @Override
  public boolean isBorderLeft() {
    return "YES".equals(check(request("is border left")));
  }
  
  @Override
  public boolean painted() {
    return "YES".equals(check(request("is painted")));
  }
  
  @Override
  public void paint() {
    check(request("paint"));
  }
  
  @Override
  public double temperature() {
    return Double.parseDouble(check(request("temperature")));
  }
  
  @Override
  public double radiation() {
    return Double.parseDouble(check(request("radiation")));
  }
}
