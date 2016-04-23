package robot.field;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Util {
  public static String readStream(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte buf[] = new byte[1024 * 8];
    while (true) {
      int count = in.read(buf);
      if (count < 0) {
        in.close();
        return new String(out.toByteArray(), "UTF-8");
      }
      out.write(buf, 0, count);
    }
  }
  
  public static void writeToStream(String str, OutputStream out) throws IOException {
    PrintStream pr = new PrintStream(out, false);
    if (str != null) pr.print(str);
    pr.flush();
    out.close();
  }
  
}
