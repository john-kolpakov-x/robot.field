package robot.field;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import robot.util.Handler;
import robot.util.PositionAbstractFrame;

public class RobotField extends PositionAbstractFrame {
  private final String name;
  
  @Override
  protected String getFrameName() {
    return name;
  }
  
  public RobotField(String name) {
    this.name = name;
    
    setTitle(name);
    
    FieldModel f = new FieldModel();
    f.colCount = 26;
    f.rowCount = 14;
    
    f.clean();
    
    final FieldPanel fieldPanel = new FieldPanel(f);
    setContentPane(fieldPanel);
    fieldPanel.update = new Handler() {
      @Override
      public void handle() {
        StringBuilder sb = new StringBuilder();
        File file = fieldPanel.selectedFile();
        if (file == null) {
          sb.append("Robot");
        } else {
          sb.append("Robot - " + file.getName());
        }
        
        if (fieldPanel.getServerPort() != null) {
          sb.append("; PORT " + fieldPanel.getServerPort());
        }
        setTitle(sb.toString());
      }
    };
    
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        getContentPane().dispatchEvent(e);
      }
    });
    
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        RobotField pan = new RobotField("Test robot");
        pan.setLocation(10, 10);
        pan.setSize(800, 400);
        try {
          pan.readBoundsInner();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        pan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pan.setVisible(true);
      }
    });
    
  }
  
}
