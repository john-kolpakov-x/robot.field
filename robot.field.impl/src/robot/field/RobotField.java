package robot.field;

import robot.util.PositionAbstractFrame;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class RobotField extends PositionAbstractFrame {
  private final String name;

  private FieldPanel fieldPanel = null;

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

    fieldPanel = new FieldPanel(f);
    setContentPane(fieldPanel);
    fieldPanel.update = () -> {
      StringBuilder sb = new StringBuilder();
      File file = fieldPanel.selectedFile();
      if (file == null) {
        sb.append("Robot");
      } else {
        sb.append("Robot - ").append(file.getName());
      }

      if (fieldPanel.getServerPort() != null) {
        sb.append("; PORT ").append(fieldPanel.getServerPort());
      }
      setTitle(sb.toString());
    };

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        getContentPane().dispatchEvent(e);
      }
    });

  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      final RobotField pan = new RobotField("Test robot");
      pan.setLocation(10, 10);
      pan.setSize(800, 400);
      try {
        pan.readBoundsInner();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      pan.setDefaultCloseOperation(EXIT_ON_CLOSE);

      pan.setVisible(true);


      SwingUtilities.invokeLater(() -> {
        try {
          pan.fieldPanel.startServer(1921);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    });

  }

}
