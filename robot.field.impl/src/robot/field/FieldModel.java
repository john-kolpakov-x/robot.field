package robot.field;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FieldModel {
  public int colCount, rowCount;
  public int robotX = 1, robotY = 1;
  
  public enum RobotStatus {
    NORMAL, BOOM_TOP, BOOM_RIGHT, BOOM_BOTTOM, BOOM_LEFT;
  }
  
  public RobotStatus robotStatus = RobotStatus.BOOM_RIGHT;
  
  private static class Point {
    final int x, y;
    
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    public Point(String str) {
      String[] split = str.trim().split("\\s");
      x = Integer.parseInt(split[0]);
      y = Integer.parseInt(split[1]);
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
    
    public String asStr() {
      return x + " " + y;
    }
    
  }
  
  private static class Cell {
    boolean pained = false, borderTop = false, borderLeft = false;
    double radiation = 0.0, temperature = 0.0;
    
    boolean isClean() {
      return !pained && !borderTop && !borderLeft && radiation == 0.0 && temperature == 0.0;
    }
    
    public Cell() {}
    
    public Cell(String str) {
      for (String eq : str.split(";")) {
        String[] split = eq.split("=");
        String name = split[0].trim();
        String value = split[1].trim();
        assign(name, value);
      }
    }
    
    private void assign(String name, String value) {
      switch (name) {
      case "painted":
        pained = Boolean.parseBoolean(value);
        return;
        
      case "borderTop":
        borderTop = Boolean.parseBoolean(value);
        return;
        
      case "borderLeft":
        borderLeft = Boolean.parseBoolean(value);
        return;
        
      case "radiation":
        radiation = Double.parseDouble(value);
        return;
        
      case "temperature":
        temperature = Double.parseDouble(value);
        return;
        
      default:
        throw new IllegalArgumentException("Unknown name = " + name);
      }
    }
    
    public String asStr() {
      return "painted=" + pained + ";borderTop=" + borderTop + ";borderLeft=" + borderLeft
          + ";radiation=" + radiation + ";temperature=" + temperature;
    }
  }
  
  private final Map<Point, Cell> field = new HashMap<>();
  
  public void setPainted(int x, int y, boolean painted) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) {
      if (!painted) return;
      cell = new Cell();
      field.put(p, cell);
    }
    cell.pained = painted;
    if (cell.isClean()) field.remove(p);
  }
  
  public boolean getPainted(int x, int y) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) return false;
    return cell.pained;
  }
  
  public void setBorderTop(int x, int y, boolean borderTop) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) {
      if (!borderTop) return;
      cell = new Cell();
      field.put(p, cell);
    }
    cell.borderTop = borderTop;
    if (cell.isClean()) field.remove(p);
  }
  
  public boolean getBorderTop(int x, int y) {
    if (y <= 0) return true;
    if (y >= rowCount) return true;
    
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) return false;
    return cell.borderTop;
  }
  
  public void setBorderLeft(int x, int y, boolean borderLeft) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) {
      if (!borderLeft) return;
      cell = new Cell();
      field.put(p, cell);
    }
    cell.borderLeft = borderLeft;
    if (cell.isClean()) field.remove(p);
  }
  
  public boolean getBorderLeft(int x, int y) {
    if (x <= 0) return true;
    if (x >= colCount) return true;
    
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) return false;
    return cell.borderLeft;
  }
  
  public void setBorderRight(int x, int y, boolean borderLeft) {
    setBorderLeft(x + 1, y, borderLeft);
  }
  
  public boolean getBorderRight(int x, int y) {
    return getBorderLeft(x + 1, y);
  }
  
  public void setBorderBottom(int x, int y, boolean borderBottom) {
    setBorderTop(x, y + 1, borderBottom);
  }
  
  public boolean getBorderBottom(int x, int y) {
    return getBorderTop(x, y + 1);
  }
  
  public void setTemperature(int x, int y, double temperature) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) {
      if (temperature == 0.0) return;
      cell = new Cell();
      field.put(p, cell);
    }
    
    cell.temperature = temperature;
    if (temperature == 0.0 && cell.isClean()) field.remove(p);
  }
  
  public double getTemperature(int x, int y) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) return 0.0;
    return cell.temperature;
  }
  
  public void setRadiation(int x, int y, double radiation) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) {
      if (radiation == 0.0) return;
      cell = new Cell();
      field.put(p, cell);
    }
    
    cell.radiation = radiation;
    if (radiation == 0.0 && cell.isClean()) field.remove(p);
  }
  
  public double getRadiation(int x, int y) {
    Point p = new Point(x, y);
    Cell cell = field.get(p);
    if (cell == null) return 0.0;
    return cell.radiation;
  }
  
  public void saveToStream(OutputStream outs) {
    try {
      PrintStream out = new PrintStream(outs, false, "UTF-8");
      try {
        saveTo(out);
      } finally {
        out.flush();
        out.close();
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void saveTo(PrintStream out) {
    out.println("rowColCount ~ " + rowCount + " " + colCount);
    out.println("robotXY ~ " + robotX + " " + robotY);
    for (Entry<Point, Cell> e : field.entrySet()) {
      out.println("cell ~ " + e.getKey().asStr() + " ~ " + e.getValue().asStr());
    }
  }
  
  public void clean() {
    robotX = robotY = 1;
    robotStatus = RobotStatus.NORMAL;
    field.clear();
  }
  
  public void loadFromStream(InputStream in) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    String line = null;
    while ((line = br.readLine()) != null) {
      readLine(line);
    }
    br.close();
  }
  
  private void readLine(String line) {
    line = line.trim();
    if (line.length() == 0) return;
    if (line.startsWith("#")) return;
    
    String[] parts = line.split("~");
    String command = parts[0].trim();
    
    if ("rowColCount".equals(command)) {
      String[] split = parts[1].trim().split("\\s+");
      rowCount = Integer.parseInt(split[0]);
      colCount = Integer.parseInt(split[1]);
      return;
    }
    
    if ("robotXY".equals(command)) {
      String[] split = parts[1].trim().split("\\s+");
      robotX = Integer.parseInt(split[0]);
      robotY = Integer.parseInt(split[1]);
      return;
    }
    
    if ("cell".equals(command)) {
      String pointStr = parts[1];
      String cellStr = parts[2];
      Point point = new Point(pointStr);
      Cell cell = new Cell(cellStr);
      field.put(point, cell);
      return;
    }
    
  }
}
