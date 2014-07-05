package robot.field.robot_png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import robot.field.FieldModel.RobotStatus;

public class RobotPng {
  
  private static Map<RobotStatus, BufferedImage> loadedData = null;
  
  public static BufferedImage getRobotImage(RobotStatus robotStatus) {
    if (loadedData == null) try {
      loadData();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return loadedData.get(robotStatus);
  }
  
  private static void loadData() throws IOException {
    loadedData = new HashMap<>();
    RobotStatus[] values = RobotStatus.values();
    for (RobotStatus robotStatus : values) {
      BufferedImage image = ImageIO.read(RobotPng.class.getResource(robotStatus.name() + ".png"));
      loadedData.put(robotStatus, image);
    }
  }
}
