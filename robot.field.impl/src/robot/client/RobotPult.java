package robot.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import robot.util.PositionAbstractFrame;
import robot.util.StrGetter;

public class RobotPult extends PositionAbstractFrame {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        RobotPult m = new RobotPult();
        m.setTitle("Robot pult");
        m.setSize(800, 600);
        m.setLocation(100, 100);
        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.readBounds();
        m.setVisible(true);
      }
    });
    
  }
  
  private StrGetter host, port;
  
  @Override
  protected String getFrameName() {
    return "RobotPult";
  }
  
  public RobotPult() {
    initialize();
  }
  
  private void initialize() {
    JPanel pane = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    
    int row = 0;
    {
      JLabel b = new JLabel("Host:");
      c.gridx = 0;
      c.gridy = row;
      pane.add(b, c);
      
      final JTextField txt = new JTextField("localhost");
      c.gridx = 1;
      c.gridy = row;
      pane.add(txt, c);
      
      host = new StrGetter() {
        @Override
        public String get() {
          return txt.getText();
        }
      };
    }
    row++;
    {
      JLabel b = new JLabel("Port:");
      c.gridx = 0;
      c.gridy = row;
      pane.add(b, c);
      
      final JTextField txt = new JTextField("8080");
      c.gridx = 1;
      c.gridy = row;
      pane.add(txt, c);
      
      port = new StrGetter() {
        @Override
        public String get() {
          return txt.getText();
        }
      };
    }
    row++;
    {
      JButton b = new JButton("GO UP");
      assign(b, "goUp");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JButton b = new JButton("GO LEFT");
      assign(b, "goLeft");
      c.gridx = 0;
      c.gridy = row;
      pane.add(b, c);
    }
    {
      JButton b = new JButton("PAINT");
      assign(b, "doPaint");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    {
      JButton b = new JButton("GO RIGHT");
      assign(b, "goRight");
      c.gridx = 2;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JButton b = new JButton("GO DOWN");
      assign(b, "goDown");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    
    row++;
    {
      JLabel l = new JLabel(" ");
      c.gridx = 1;
      c.gridy = row;
      pane.add(l, c);
    }
    row++;
    
    {
      JButton b = new JButton("is border top");
      assign(b, "checkBorderTop");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JButton b = new JButton("is border left");
      assign(b, "checkBorderLeft");
      c.gridx = 0;
      c.gridy = row;
      pane.add(b, c);
    }
    {
      JButton b = new JButton("is painted");
      assign(b, "checkPainted");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    {
      JButton b = new JButton("is border right");
      assign(b, "checkBorderRight");
      c.gridx = 2;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JButton b = new JButton("is border bottom");
      assign(b, "checkBorderBottom");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JLabel l = new JLabel(" ");
      c.gridx = 1;
      c.gridy = row;
      pane.add(l, c);
    }
    row++;
    {
      JButton b = new JButton("get temperature");
      assign(b, "getTemperature");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    row++;
    {
      JButton b = new JButton("get radiation");
      assign(b, "getRadiation");
      c.gridx = 1;
      c.gridy = row;
      pane.add(b, c);
    }
    
    setContentPane(pane);
  }
  
  private void assign(JButton b, String methodName) {
    try {
      assignInner(b, methodName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private void assignInner(JButton b, String methodName) throws NoSuchMethodException {
    final Method method = getClass().getMethod(methodName);
    final Object object = this;
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          method.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
          throw new RuntimeException(e1);
        }
      }
    });
  }
  
  private Robot getRobot() {
    return new RemoteRobot(host.get(), Integer.parseInt(port.get()));
  }
  
  public void goUp() {
    getRobot().up();
  }
  
  public void goDown() {
    getRobot().down();
  }
  
  public void goRight() {
    getRobot().right();
  }
  
  public void goLeft() {
    getRobot().left();
  }
  
  public void doPaint() {
    getRobot().paint();
  }
  
  public void checkBorderTop() {
    System.out.println("border top = " + getRobot().isBorderTop());
  }
  
  public void checkBorderBottom() {
    System.out.println("border bottom = " + getRobot().isBorderBottom());
  }
  
  public void checkBorderRight() {
    System.out.println("border right = " + getRobot().isBorderRight());
  }
  
  public void checkBorderLeft() {
    System.out.println("border left = " + getRobot().isBorderLeft());
  }
  
  public void checkPainted() {
    System.out.println("painted = " + getRobot().painted());
  }
  
  public void getRadiation() {
    System.out.println("radiation = " + getRobot().radiation());
  }
  
  public void getTemperature() {
    System.out.println("temperature = " + getRobot().temperature());
  }
  
}
