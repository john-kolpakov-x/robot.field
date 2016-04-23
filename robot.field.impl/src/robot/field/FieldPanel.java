package robot.field;

import robot.field.FieldModel.RobotStatus;
import robot.field.robot_png.RobotPng;
import robot.util.Handler;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldPanel extends JPanel {
  private final Color backgroundColor = new Color(100, 200, 100);
  private final Color gridColor = new Color(0, 0, 0);

  public Handler update = null;

  private void update() {
    if (update == null) return;
    update.handle();
  }

  private final int xOffset = 2, yOffset = 2;

  private final int cellWidth = 50, cellHeight = 40;

  private final FieldModel fieldModel;

  private String label1 = "label1";
  private String label2 = "label2";
  private String label3 = "label3";

  private int selectedI = -1, selectedJ = -1;
  private File selectedFile = null;

  private File getCurrentDirStorageFile() {
    return new File(".conf/current_dir.utf8.txt");
  }

  public FieldPanel(final FieldModel fieldModel) {
    this.fieldModel = fieldModel;

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          leftMousePressed(e);
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
          rightMousePressed(e);
        }
      }
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        label1 = "MOUSE " + x + ", " + y;
        FieldPanel.this.repaint();
      }
    });

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keyPushed(fieldModel, e);
      }
    });
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(backgroundColor);
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setColor(Color.BLACK);

    {
      int left = 200;
      int top = 600;

      g.drawString(label1, left, top + 0);
      g.drawString(label2, left, top + 20);
      g.drawString(label3, left, top + 40);
    }

    g.setColor(gridColor);
    {
      int height = fieldModel.rowCount * cellHeight;
      for (int i = 0, C = fieldModel.colCount; i <= C; i++) {
        int x = xOffset + i * cellWidth;
        g.drawLine(x, yOffset, x, height + yOffset);
      }
    }
    {
      int width = fieldModel.colCount * cellWidth;
      for (int j = 0, C = fieldModel.rowCount; j <= C; j++) {
        int y = yOffset + j * cellHeight;
        g.drawLine(xOffset, y, width + xOffset, y);
      }
    }

    {
      for (int i = 0, Ci = fieldModel.colCount; i <= Ci; i++) {
        for (int j = 0, Cj = fieldModel.rowCount; j <= Cj; j++) {
          int x = xOffset + i * cellWidth;
          int y = yOffset + j * cellHeight;

          g.setColor(Color.YELLOW);
          if (i < Ci && j < Cj && selectedI == i && selectedJ == j) {
            g.fillRect(x + 1, y + 1, cellWidth - 1, cellHeight - 1);
          }
          if (fieldModel.getPainted(i, j) && i < Ci && j < Cj) {
            Graphics g2 = g.create(x + 1, y + 1, cellWidth - 1, cellHeight - 1);
            int N = 10;
            g2.setColor(Color.black);
            for (int u = 0; u < N; u++) {
              int x1 = cellWidth * 2 / N * u;
              int y2 = cellHeight * 2 / N * u;
              g2.drawLine(x1, 0, 0, y2);
            }
            g2.dispose();
          }
          g.setColor(Color.RED);
          if (fieldModel.getBorderLeft(i, j) && j < Cj) {
            g.drawLine(x - 1, y, x - 1, y + cellHeight);
            g.drawLine(x + 0, y, x + 0, y + cellHeight);
            g.drawLine(x + 1, y, x + 1, y + cellHeight);
          }
          if (fieldModel.getBorderTop(i, j) && i < Ci) {
            g.drawLine(x, y - 1, x + cellWidth, y - 1);
            g.drawLine(x, y - 0, x + cellWidth, y - 0);
            g.drawLine(x, y + 1, x + cellWidth, y + 1);
          }
        }
      }
    }

    {
      BufferedImage image = RobotPng.getRobotImage(fieldModel.robotStatus);
      int i = fieldModel.robotX;
      int j = fieldModel.robotY;

      int x = xOffset + i * cellWidth;
      int y = yOffset + j * cellHeight;

      Graphics g2 = g.create(x + 1, y + 1, cellWidth - 1, cellHeight - 1);
      int xC = cellWidth / 2 - image.getWidth() / 2;
      int yC = cellHeight / 2 - image.getHeight() / 2;
      g2.drawImage(image, xC, yC, null);
      g2.dispose();
    }

  }

  private void rightMousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int top = y - yOffset;

    boolean borderTop = false, borderLeft = false;

    int j = -1, jMod = -1;
    if (top >= 0) {
      j = top / cellHeight;
      jMod = top % cellHeight;
    }

    int left = x - xOffset;

    int i = -1, iMod = -1;
    if (left >= 0) {
      i = left / cellWidth;
      iMod = left % cellWidth;
    }

    int save_di = i, save_dj = j;

    if (0 <= jMod && jMod <= 3) {
      borderTop = true;
    } else {
      int tmp = cellHeight - jMod;
      if (1 <= tmp && tmp <= 4) {
        j++;
        borderTop = true;
      }
    }

    if (0 <= iMod && iMod <= 3) {
      borderLeft = true;
    } else {
      int tmp = cellWidth - iMod;
      if (1 <= tmp && tmp <= 4) {
        i++;
        borderLeft = true;
      }
    }

    if (borderLeft && borderTop) {
      borderLeft = borderTop = false;
      i = save_di;
      j = save_dj;
    }

    if (borderLeft) return;
    if (borderTop) return;

    selectedI = i;
    selectedJ = j;

    label3 = "selected (I, J) = (" + selectedI + ", " + selectedJ + ")";
    repaint();
  }

  private void leftMousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    int top = y - yOffset;

    boolean borderTop = false, borderLeft = false;

    int j = -1, jMod = -1;
    if (top >= 0) {
      j = top / cellHeight;
      jMod = top % cellHeight;
    }

    int left = x - xOffset;

    int i = -1, iMod = -1;
    if (left >= 0) {
      i = left / cellWidth;
      iMod = left % cellWidth;
    }

    int save_di = i, save_dj = j;

    if (0 <= jMod && jMod <= 3) {
      borderTop = true;
    } else {
      int tmp = cellHeight - jMod;
      if (1 <= tmp && tmp <= 4) {
        j++;
        borderTop = true;
      }
    }

    if (0 <= iMod && iMod <= 3) {
      borderLeft = true;
    } else {
      int tmp = cellWidth - iMod;
      if (1 <= tmp && tmp <= 4) {
        i++;
        borderLeft = true;
      }
    }

    if (borderLeft && borderTop) {
      borderLeft = borderTop = false;
      i = save_di;
      j = save_dj;
    }

    if (borderLeft) {
      boolean w = fieldModel.getBorderLeft(i, j);
      fieldModel.setBorderLeft(i, j, !w);
    }
    if (borderTop) {
      boolean w = fieldModel.getBorderTop(i, j);
      fieldModel.setBorderTop(i, j, !w);
    }

    if (!borderTop && !borderLeft) {
      boolean w = fieldModel.getPainted(i, j);
      fieldModel.setPainted(i, j, !w);
    }

    label2 = "j = " + j + ", jMod = " + jMod + ", i = " + i + ", iMod = " + iMod;
    label3 = "borderLeft = " + borderLeft + ", borderTop = " + borderTop;
    repaint();
  }

  private void keyPushed(final FieldModel fieldModel, KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_SPACE) {
      fieldModel.robotX = selectedI;
      fieldModel.robotY = selectedJ;
      fieldModel.robotStatus = RobotStatus.NORMAL;
      repaint();
      return;
    }

    if (keyCode == KeyEvent.VK_S && e.isControlDown()) {
      saveAs();
      return;
    }
    if (keyCode == KeyEvent.VK_S && !e.isControlDown()) {
      save();
      return;
    }
    if (keyCode == KeyEvent.VK_L && e.isControlDown()) {
      loadFromFile();
      return;
    }
    if (keyCode == KeyEvent.VK_L && !e.isControlDown()) {
      load();
      return;
    }

    if (keyCode == KeyEvent.VK_N && !e.isControlDown()) {
      createNew();
      return;
    }

    if (keyCode == KeyEvent.VK_P && !e.isControlDown()) {
      resetServer();
      return;
    }

    System.out.println("keyCode = " + keyCode);
  }

  private void load() {
    try {
      loadInner();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void saveAs() {
    try {
      saveAsInner();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private JFileChooser getFileChooser() throws IOException {
    JFileChooser fc = new JFileChooser();
    prepareFileFilters(fc);

    {
      File file = getCurrentDirStorageFile();
      if (file.exists()) {
        fc.setCurrentDirectory(new File(Util.readStream(new FileInputStream(file))));
      }
    }
    return fc;
  }

  private void saveInner() throws FileNotFoundException {
    fieldModel.saveToStream(new FileOutputStream(selectedFile));
    label3 = "saved to " + selectedFile;
    repaint();
  }

  private void save() {
    try {
      if (selectedFile == null) {
        saveAsInner();
      } else {
        saveInner();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void prepareFileFilters(JFileChooser fc) {
    FileFilter filter = new FileFilter() {
      @Override
      public String getDescription() {
        return "Robot fields (*.robot_field)";
      }

      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) return true;
        return f.getAbsolutePath().endsWith(".robot_field");
      }
    };
    while (fc.getChoosableFileFilters().length > 0) {
      fc.removeChoosableFileFilter(fc.getChoosableFileFilters()[0]);
    }
    fc.addChoosableFileFilter(filter);
    fc.setFileFilter(filter);
  }

  private void loadFromFile() {
    try {
      loadFromFileInner();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void saveAsInner() throws Exception {
    JFileChooser fc = getFileChooser();

    if (selectedFile != null) {
      fc.setSelectedFile(selectedFile);
    }

    if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File selFile = fc.getSelectedFile();

    if (!selFile.getAbsolutePath().endsWith(".robot_field")) {
      selFile = new File(selFile.getAbsolutePath() + ".robot_field");
    }

    if (selFile.exists()) {
      if (JOptionPane.showConfirmDialog(this, "Этот файл уже существует. Переписать его?",
        "Предупреждение", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
    }

    selectedFile = selFile;

    System.out.println("selFile = " + selFile);

    {
      File confFile = getCurrentDirStorageFile();
      confFile.getParentFile().mkdirs();
      Util.writeToStream(selFile.getAbsolutePath(), new FileOutputStream(confFile));
    }

    saveInner();

    update();
  }

  private void loadFromFileInner() throws Exception {
    JFileChooser fc = getFileChooser();

    if (selectedFile != null) {
      fc.setSelectedFile(selectedFile);
    }

    if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File selFile = fc.getSelectedFile();

    if (!selFile.exists()) {
      JOptionPane.showMessageDialog(this, "Не существует выбранного файла " + selFile, "Ошибка",
        JOptionPane.ERROR_MESSAGE);
      return;
    }

    selectedFile = selFile;

    loadInner();

    update();
  }

  private void loadInner() throws Exception {
    fieldModel.clean();
    if (selectedFile != null) fieldModel.loadFromStream(new FileInputStream(selectedFile));
    repaint();
  }

  public File selectedFile() {
    return selectedFile;
  }

  private static final Pattern D_D = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s*");

  private void createNew() {
    String res = "14 26";
    while (true) {
      res = JOptionPane.showInputDialog(this, "Укажите размер поля (строк столбцов)", res);
      if (res == null) return;

      Matcher m = D_D.matcher(res);
      if (!m.matches()) {
        showInputErrorMessage();
        continue;
      }

      int a1 = Integer.parseInt(m.group(1));
      int a2 = Integer.parseInt(m.group(2));

      if (a1 < 3 || a2 < 3) {
        showInputErrorMessage();
        continue;
      }

      fieldModel.clean();
      fieldModel.rowCount = a1;
      fieldModel.colCount = a2;
      selectedFile = null;
      repaint();

      update();

      break;
    }
  }

  private void showInputErrorMessage() {
    JOptionPane.showMessageDialog(this, "Необходимо ввести два целых числа через пробел."
      + " Каждое число должно быть > 2 (больше двух)", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
  }

  private void resetServer() {
    try {
      resetServerInner();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ServerSocket serverSocket = null;

  public Integer getServerPort() {
    if (serverSocket == null) return null;
    if (serverSocket.isClosed()) return null;
    return serverSocket.getLocalPort();
  }

  private void resetServerInner() throws Exception {
    String strPort = "8080";
    if (serverSocket != null && !serverSocket.isClosed()) {
      strPort = "" + serverSocket.getLocalPort();
    }
    while (true) {
      strPort = JOptionPane.showInputDialog(this, "Укажите порт сервера;"
        + " или пустую сторку, если сервер надо выключить", strPort);
      if (strPort == null) return;
      strPort = strPort.trim();

      if (strPort.length() == 0) {
        closeServer();
        update();
        return;
      }

      final int newPort;
      try {
        newPort = Integer.parseInt(strPort);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Необходимо ввести число", "Ошибка ввода",
          JOptionPane.ERROR_MESSAGE);
        continue;
      }

      if (newPort <= 1024 || newPort >= 65536) {
        JOptionPane.showMessageDialog(this, "Порт может быть в диапазоне от 1025 до 65535",
          "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        continue;
      }

      closeServer();

      if (startServer(newPort)) break;
    }
  }

  public boolean startServer(int port) throws IOException {
    try {
      serverSocket = new ServerSocket(port);
      new Thread(() -> {
        try {
          readSocket();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).start();
      update();
      return true;
    } catch (SecurityException e) {
      JOptionPane.showMessageDialog(this, "Порт занят: " + e.getMessage(), "Ошибка ввода",
        JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  private void closeServer() throws IOException {
    if (serverSocket == null) return;
    if (!serverSocket.isClosed()) {
      serverSocket.close();
    }
    serverSocket = null;
  }

  private void readSocket() throws Exception {
    ServerSocket ss = serverSocket;
    if (ss == null) return;
    while (!ss.isClosed()) {
      Socket s = ss.accept();
      BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
      final String line = rd.readLine();
      final String response[] = new String[]{null};
      SwingUtilities.invokeAndWait(() -> response[0] = executeRequest(line));
      PrintStream out = new PrintStream(s.getOutputStream(), false, "UTF-8");
      out.println(response[0]);
      out.flush();
      s.close();
    }
  }

  private String executeRequest(String in) {
    try {
      String ret = executeRequestInner(in);
      if (ret == null) ret = "OK";
      Thread.sleep(100);
      return ret;
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  private void right() {
    if (fieldModel.getBorderRight(fieldModel.robotX, fieldModel.robotY)) {
      fieldModel.robotStatus = RobotStatus.BOOM_RIGHT;
      repaint();
      throw new Boom();
    }
    fieldModel.robotX++;
    fieldModel.robotStatus = RobotStatus.NORMAL;
    repaint();
  }

  private void left() {
    if (fieldModel.getBorderLeft(fieldModel.robotX, fieldModel.robotY)) {
      fieldModel.robotStatus = RobotStatus.BOOM_LEFT;
      repaint();
      throw new Boom();
    }
    fieldModel.robotX--;
    fieldModel.robotStatus = RobotStatus.NORMAL;
    repaint();
  }

  private void down() {
    if (fieldModel.getBorderBottom(fieldModel.robotX, fieldModel.robotY)) {
      fieldModel.robotStatus = RobotStatus.BOOM_BOTTOM;
      repaint();
      throw new Boom();
    }
    fieldModel.robotY++;
    fieldModel.robotStatus = RobotStatus.NORMAL;
    repaint();
  }

  private void up() {
    if (fieldModel.getBorderTop(fieldModel.robotX, fieldModel.robotY)) {
      fieldModel.robotStatus = RobotStatus.BOOM_TOP;
      repaint();
      throw new Boom();
    }
    fieldModel.robotY--;
    fieldModel.robotStatus = RobotStatus.NORMAL;
    repaint();
  }

  private String executeRequestInner(String in) {
    if ("go up".equals(in)) {
      up();
      return null;
    }
    if ("go down".equals(in)) {
      down();
      return null;
    }
    if ("go right".equals(in)) {
      right();
      return null;
    }
    if ("go left".equals(in)) {
      left();
      return null;
    }

    if ("is border top".equals(in)) {
      return fieldModel.getBorderTop(fieldModel.robotX, fieldModel.robotY) ? "YES" : "NO";
    }
    if ("is border bottom".equals(in)) {
      return fieldModel.getBorderBottom(fieldModel.robotX, fieldModel.robotY) ? "YES" : "NO";
    }
    if ("is border left".equals(in)) {
      return fieldModel.getBorderLeft(fieldModel.robotX, fieldModel.robotY) ? "YES" : "NO";
    }
    if ("is border right".equals(in)) {
      return fieldModel.getBorderRight(fieldModel.robotX, fieldModel.robotY) ? "YES" : "NO";
    }

    if ("is painted".equals(in)) {
      return fieldModel.getPainted(fieldModel.robotX, fieldModel.robotY) ? "YES" : "NO";
    }
    if ("paint".equals(in)) {
      fieldModel.setPainted(fieldModel.robotX, fieldModel.robotY, true);
      repaint();
      return null;
    }

    if ("temperature".equals(in)) {
      return "" + fieldModel.getTemperature(fieldModel.robotX, fieldModel.robotY);
    }
    if ("radiation".equals(in)) {
      return "" + fieldModel.getRadiation(fieldModel.robotX, fieldModel.robotY);
    }

    throw new IllegalArgumentException("Unknown command " + in);
  }
}
