package robot.field;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConfData {
  private final Map<String, String> data = new HashMap<>();
  
  public String str(String name) {
    String ret = data.get(name);
    if (ret == null) return "";
    return ret;
  }
  
  public void setStr(String key, String value) {
    data.put(key, value);
  }
  
  public void saveTo(OutputStream out) throws Exception {
    PrintStream pr = new PrintStream(out, false, "UTF-8");
    
    for (Entry<String, String> e : data.entrySet()) {
      String value = e.getValue();
      if (value == null) value = "";
      value = value.replaceAll("\r", "\\r");
      value = value.replaceAll("\n", "\\n");
      pr.println(e.getKey() + "=" + value);
    }
    
    pr.flush();
    pr.close();
  }
  
  public void readFrom(InputStream in) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    String line = null;
    while ((line = br.readLine()) != null) {
      //variable line does NOT have new-line-character at the end
      System.out.println(line);
    }
    br.close();
  }
}
