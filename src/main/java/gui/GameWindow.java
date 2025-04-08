package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
/**
 * Класс GameWindow отображает игровое поле
 * и обновляется при изменении
 */
public class GameWindow extends JInternalFrame {
    /**
     * Массив данных окна: [x, y, width, height, state].
     */
    private int[] gameData = new int[]{100, 100, 1100, 440, 1};

    /**
     * Конструктор окна игрового поля.
     * Теперь принимает общий экземпляр RobotModel и передаёт его в GameVisualizer.
     *
     * @param model общий экземпляр модели робота
     * @throws PropertyVetoException если установка икон (свернутости) окна не разрешена
     */
    public GameWindow(RobotModel model) throws PropertyVetoException {
        super("Игровое поле", true, true, true, true);

        setSize(gameData[2], gameData[3]);
        setLocation(gameData[0], gameData[1]);
        setResizable(true);

        // Создаем визуализатор (View)
        GameVisualizer visualizer = new GameVisualizer(model);
        visualizer.setPreferredSize(new Dimension(1100, 440));

        // Создаем контроллер (Presenter), передаем модель и вьюшку
        GameController controller = new GameController(model, visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
    }

    /**
     * Устанавливает данные окна (координаты, размеры и состояние).
     */
    public void setGameData(int[] gameData) {
        this.gameData = gameData;
    }

    /**
     * Возвращает массив данных окна (координаты, размеры и состояние).
     */
    public int[] getGameData() {
        return gameData;
    }
}
