package robot.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class RobotClient {
  public static void main(String[] args) throws Exception {
    Socket s = new Socket("localhost", 8080);
    PrintStream out = new PrintStream(s.getOutputStream(), false, "UTF-8");
    out.println("go right");
    out.flush();
    
    BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
    String outLine = rd.readLine();
    
    System.out.println("outLine = " + outLine);
    
    s.close();
  }
}
