package robot.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import javax.swing.JFrame;

import robot.field.Util;

public abstract class PositionAbstractFrame extends JFrame {
  protected abstract String getFrameName();
  
  private boolean cannotSaveBounds = true;
  
  private File getBoundsFile() {
    return new File(".conf/bounds_" + getFrameName() + ".utf8.txt");
  }
  
  public PositionAbstractFrame() {
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentMoved(ComponentEvent e) {
        if (cannotSaveBounds) return;
        try {
          saveBounds();
        } catch (Exception e1) {
          throw new RuntimeException(e1);
        }
      }
      
      @Override
      public void componentResized(ComponentEvent e) {
        if (cannotSaveBounds) return;
        try {
          saveBounds();
        } catch (Exception e1) {
          throw new RuntimeException(e1);
        }
      }
    });
  }
  
  private void saveBounds() throws Exception {
    File file = getBoundsFile();
    file.getParentFile().mkdirs();
    PrintStream out = new PrintStream(file, "UTF-8");
    Point p = getLocation();
    Dimension s = getSize();
    out.print(p.x + " " + p.y + " " + s.width + " " + s.height);
    out.close();
  }
  
  public void readBounds() {
    try {
      readBoundsInner();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public void readBoundsInner() throws Exception {
    
    File file = getBoundsFile();
    if (!file.exists()) {
      cannotSaveBounds = false;
      return;
    }
    String str = Util.readStream(new FileInputStream(file));
    String[] split = str.split(" ");
    
    setLocation(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    setSize(Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    
    cannotSaveBounds = false;
  }
  
}
