package gui;

import model.LocalizationManager;
import model.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Класс RobotInfoWindow представляет окно с информацией о роботе.
 * Окно наследуется от JInternalFrame и отображает текущие координаты и направление робота.
 * Данные окна (координаты, размеры и состояние) хранятся в массиве windowData.
 * Окно подписывается на изменения модели робота и обновляет отображаемую информацию при их возникновении.
 */
public class RobotInfoWindow extends JInternalFrame implements PropertyChangeListener {
    private JLabel positionLabel;
    private final RobotModel robotModel;

    private int[] windowData = new int[]{100, 100, 300, 100, 1};

    /**
     * Конструктор окна информации о роботе. Создает окно с заголовком "Информация о роботе",
     * устанавливает необходимые флаги (возможность перемещения, закрытия, изменения размера и сворачивания),
     * сохраняет переданную модель робота и подписывается на ее изменения. Затем инициализирует пользовательский интерфейс,
     * упаковывает окно и делает его видимым.
     */
    public RobotInfoWindow(RobotModel model) {
        super(LocalizationManager.getInstance().getString("menu.robot"), true, true, true, true);
        this.robotModel = model;
        robotModel.addPropertyChangeListener(this);
        // Подписываемся на смену локали
        LocalizationManager.getInstance().addPropertyChangeListener(this);

        initUI();
        pack();
        setVisible(true);
    }

    /**
     * Инициализирует пользовательский интерфейс окна.
     * Окно делится на две части с помощью сетки, в которых отображается информация о позиции и направлении робота.
     */
    private void initUI() {
        setLayout(new GridLayout(2, 1));
        positionLabel = new JLabel();
        updateLabels();
        add(positionLabel);
    }

    /**
     * Обновляет текст меток, отображающих позицию и направление робота.
     * Используется форматирование с двумя знаками после запятой.
     */
    private void updateLabels() {
        positionLabel.setText(String.format(LocalizationManager.getInstance().getString("robot.info")
                        + ": (%.2f, %.2f)",
                robotModel.getPositionX(), robotModel.getPositionY()));
    }

    /**
     * Обрабатывает событие изменения свойств модели робота.
     * При возникновении события обновляет отображаемые данные в окне.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            // Обновляем заголовок окна
            setTitle(LocalizationManager.getInstance().getString("menu.robot"));

            updateLabels();
        }
        if ("position".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::updateLabels);
        }
    }

    /**
     * Устанавливает сохраненные данные окна (координаты, размеры и состояние).
     * После установки данных окно перемещается, изменяется его размер и состояние сворачивания.
     * Если установка сворачиваемости вызывает исключение, оно игнорируется.
     */
    public void setRobotInfoData(int[] data) {
        this.windowData = data;
        setLocation(data[0], data[1]);
        setSize(data[2], data[3]);
        try {
            setIcon(data[4] == 0);
        } catch (Exception e) {
            // игнорируем
        }
    }

    /**
     * Возвращает массив данных окна, содержащий информацию о его расположении, размере и состоянии.
     */
    public int[] getWindowData() {
        return windowData;
    }


}
