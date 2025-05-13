package logic;

import gui.*;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Locale;

/**
 * WindowsSaver отвечает за сохранение и загрузку состояния окон
 * (GameWindow, LogWindow и RobotInfoWindow) в файл конфигурации state.cfg.
 */
public class WindowsSaver {
    /**
     * Сохраняет состояние внутреннего окна (JInternalFrame).
     */
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
     * Сохраняет состояние главного окна (JFrame).
     */
    public int[] saveWidowData(JFrame frame) {
        int windowState = frame.getExtendedState();
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
    public void saveToFile(MainApplicationFrame mainFrame, GameWindow gameWindow, LogWindow logWindow, RobotInfoWindow infoWindow) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("state.cfg"))) {
            writer.write("mainFrame=" + Arrays.toString(saveWidowData(mainFrame)));
            writer.newLine();
            writer.write("gameWindow=" + Arrays.toString(saveWidowData(gameWindow)));
            writer.newLine();
            writer.write("logWindow=" + Arrays.toString(saveWidowData(logWindow)));
            writer.newLine();
            writer.write("infoWindow=" + Arrays.toString(saveWidowData(infoWindow)));
            writer.newLine();
            writer.write("language=" + LocalizationManager.getInstance().getCurrentLocale().getLanguage());
            writer.newLine();
        }
    }

    /**
     * Загружает данные о положении и размере окон из файла "state.cfg".
     */
    public void loadFromFile(MainApplicationFrame mainFrame, GameWindow gameWindow, LogWindow logWindow,
                             RobotInfoWindow infoWindow) throws IOException {
        File configFile = new File("state.cfg");

        if (!configFile.exists()) {
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            gameWindow.setBounds(100, 100, 400, 300);
            logWindow.setBounds(150, 150, 300, 200);
            infoWindow.setBounds(200, 200, 300, 200);

            mainFrame.setExtendedState(JFrame.NORMAL);
            gameWindow.setVisible(true);
            logWindow.setVisible(true);
            infoWindow.setVisible(true);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("state.cfg"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("mainFrame=")) {
                    int[] mainFrameData = parseData(line.substring("mainFrame=".length()));
                    mainFrame.setBounds(mainFrameData[0], mainFrameData[1], mainFrameData[2], mainFrameData[3]);
                    mainFrame.setExtendedState(mainFrameData[4]);
                } else if (line.startsWith("gameWindow=")) {
                    int[] gameData = parseData(line.substring("gameWindow=".length()));
                    gameWindow.setGameData(gameData);
                    gameWindow.setBounds(gameData[0], gameData[1], gameData[2], gameData[3]);
                    gameWindow.setVisible(gameData[4] == 1);
                } else if (line.startsWith("logWindow=")) {
                    int[] logData = parseData(line.substring("logWindow=".length()));
                    logWindow.setLogData(logData);
                    logWindow.setBounds(logData[0], logData[1], logData[2], logData[3]);
                    logWindow.setVisible(logData[4] == 1);
                } else if (line.startsWith("infoWindow=")) {
                    int[] infoData = parseData(line.substring("infoWindow=".length()));
                    infoWindow.setRobotInfoData(infoData);
                    infoWindow.setBounds(infoData[0], infoData[1], infoData[2], infoData[3]);
                    infoWindow.setVisible(infoData[4] == 1);
                } else if (line.startsWith("language=")) {
                    String lang = line.substring("language=".length()).trim();
                    LocalizationManager.getInstance().setLocale(Locale.of(lang));
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
