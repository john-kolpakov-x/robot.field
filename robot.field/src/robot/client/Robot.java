package robot.client;

public interface Robot {
  void up();
  
  void right();
  
  void down();
  
  void left();
  
  boolean isBorderTop();
  
  boolean isBorderRight();
  
  boolean isBorderBottom();
  
  boolean isBorderLeft();
  
  boolean painted();
  
  void paint();
  
  double temperature();
  
  double radiation();
}
