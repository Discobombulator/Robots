package gui;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;

/**
 * WindowsSaver отвечает за сохранение и загрузку состояния окон
 * (GameWindow, LogWindow и RobotInfoWindow) в файл конфигурации state.cfg.
 */
public class WindowsSaver {
    public int[] saveWidowData(JInternalFrame frame) {
        int windowState = frame.isIcon() ? 0 : 1;
        return new int[]{
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                windowState
        };
    }

    /**
     * Сохраняет данные о состоянии GameWindow, LogWindow и RobotInfoWindow в файл state.cfg.
     */
    public void saveToFile(GameWindow gameWindow, LogWindow logWindow, RobotInfoWindow infoWindow) throws IOException {
        int[] gameData = gameWindow.getGameData();
        int[] logData = logWindow.getLogData();
        int[] infoData = infoWindow.getWindowData();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("state.cfg"))) {
            writer.write("gameWindow=" + Arrays.toString(gameData));
            writer.newLine();
            writer.write("logWindow=" + Arrays.toString(logData));
            writer.newLine();
            writer.write("infoWindow=" + Arrays.toString(infoData));
            writer.newLine();
        }
    }

    /**
     * Загружает данные о состоянии GameWindow, LogWindow и RobotInfoWindow из файла state.cfg.
     */
    public void loadFromFile(GameWindow gameWindow, LogWindow logWindow, RobotInfoWindow infoWindow) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("state.cfg"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("gameWindow=")) {
                    String gameDataStr = line.substring("gameWindow=".length());
                    int[] gameData = parseData(gameDataStr);
                    gameWindow.setGameData(gameData);
                    gameWindow.setLocation(gameData[0], gameData[1]);
                    gameWindow.setSize(gameData[2], gameData[3]);
                    gameWindow.setVisible(gameData[4] == 1);
                } else if (line.startsWith("logWindow=")) {
                    String logDataStr = line.substring("logWindow=".length());
                    int[] logData = parseData(logDataStr);
                    logWindow.setLogData(logData);
                    logWindow.setLocation(logData[0], logData[1]);
                    logWindow.setSize(logData[2], logData[3]);
                    logWindow.setVisible(logData[4] == 1);
                } else if (line.startsWith("infoWindow=")) {
                    String infoDataStr = line.substring("infoWindow=".length());
                    int[] infoData = parseData(infoDataStr);
                    infoWindow.setRobotInfoData(infoData);
                    infoWindow.setVisible(infoData[4] == 1);
                }
            }
        }
    }
    /**
     * Чистит данные от лишних символов, после чтения из файла.
     */
    private int[] parseData(String dataStr) {
        String[] dataParts = dataStr.replace("[", "").replace("]", "").split(", ");
        int[] data = new int[dataParts.length];
        for (int i = 0; i < dataParts.length; i++) {
            data[i] = Integer.parseInt(dataParts[i]);
        }
        return data;
    }
}
