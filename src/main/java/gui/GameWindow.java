package gui;

import java.awt.*;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 * Окно игрового поля приложения.
 * Класс GameWindow наследуется от JInternalFrame и содержит визуализатор игрового поля.
 * Данные о положении, размере и состоянии окна хранятся в массиве {@code gameData}.
 */
public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;
    /**
     * Массив данных окна: [x, y, width, height, state].
     * state: 1 - окно развернуто, 0 - окно свернуто.
     */
    private int[] gameData = new int[]{100, 100, 1100, 440, 1};

    /**
     * Устанавливает данные о состоянии окна.
     *
     * @param gameData массив данных окна [x, y, width, height, state]
     */
    public void setGameData(int[] gameData) {
        this.gameData = gameData;
    }

    /**
     * Возвращает данные о состоянии окна.
     *
     * @return массив данных окна [x, y, width, height, state]
     */
    public int[] getGameData() {
        return gameData;
    }

    /**
     * Конструктор окна игрового поля.
     * Устанавливает размеры, положение, состояние окна и инициализирует визуализатор.
     *
     * @throws PropertyVetoException если установка икон (свернутости) окна не разрешена
     */
    public GameWindow() throws PropertyVetoException {
        super("Игровое поле", true, true, true, true);

        setSize(gameData[2], gameData[3]);
        setLocation(gameData[0], gameData[1]);
        setResizable(true);

        m_visualizer = new GameVisualizer();
        m_visualizer.setPreferredSize(getSize());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
    }

}
