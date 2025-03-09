package gui;

import java.io.*;
import java.util.Arrays;

/**
 * WindowsLocation отвечает за сохранение и загрузку состояния окон
 * (GameWindow и LogWindow) в файл конфигурации state.cfg.
 */
public class WindowsLocation {
    /**
     * Сохраняет данные о состоянии GameWindow и LogWindow в файл state.cfg.
     *
     * @param gameWindow окно с игровым полем
     * @param logWindow  окно протокола работы
     * @throws IOException если возникает ошибка ввода-вывода при записи файла
     */
    public void saveToFile(GameWindow gameWindow, LogWindow logWindow) throws IOException {
        int[] gameData = gameWindow.getGameData();
        int[] logData = logWindow.getLogData();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("state.cfg"))) {
            writer.write("gameWindow=" + Arrays.toString(gameData));
            writer.newLine();
            writer.write("logWindow=" + Arrays.toString(logData));
            writer.newLine();
        }
    }

    /**
     * Загружает данные о состоянии GameWindow и LogWindow из файла state.cfg.
     * После загрузки устанавливает параметры расположения, размера и видимости окон.
     *
     * @param gameWindow окно с игровым полем
     * @param logWindow  окно протокола работы
     */
    public void loadFromFile(GameWindow gameWindow, LogWindow logWindow) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("state.cfg"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("gameWindow=")) {
                    String gameDataStr = line.substring("gameWindow=".length());
                    int[] gameData = parseData(gameDataStr);
                    // Устанавливаем данные в объект GameWindow и обновляем его параметры:
                    gameWindow.setGameData(gameData);
                    gameWindow.setLocation(gameData[0], gameData[1]);
                    gameWindow.setSize(gameData[2], gameData[3]);
                    gameWindow.setVisible(gameData[4] == 1);
                } else if (line.startsWith("logWindow=")) {
                    String logDataStr = line.substring("logWindow=".length());
                    int[] logData = parseData(logDataStr);
                    // Устанавливаем данные в объект LogWindow и обновляем его параметры:
                    logWindow.setLogData(logData);
                    logWindow.setLocation(logData[0], logData[1]);
                    logWindow.setSize(logData[2], logData[3]);
                    logWindow.setVisible(logData[4] == 1);
                }
            }
        }
    }

    /**
     * Парсит строку с данными в массив int[].
     * Ожидаемый формат строки: "[x, y, width, height, state]".
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
