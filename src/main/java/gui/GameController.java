package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;


public class GameController extends JPanel {
    private final Timer timer = new Timer("events generator", true);
    private RobotModel robotModel;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    /**
     * Устанавливает общий экземпляр модели робота, который будет использоваться
     * для обновления и отрисовки.
     */
    public void setRobotModel(RobotModel model) {
        this.robotModel = model;
        // Подписываемся на изменения модели для вызова перерисовки.
        robotModel.addPropertyChangeListener(evt -> repaint());
    }


    public int getTargetPositionY() {
        return targetPositionY;
    }

    public int getTargetPositionX() {
        return targetPositionX;
    }

    public RobotModel getRobotModel() {
        return robotModel;
    }


    /**
     * Конструктор GameVisualizer. Инициализирует таймеры для перерисовки и обновления модели,
     * а также обрабатывает события кликов для задания новой целевой позиции.
     */
    public GameController() {
        // Таймер для регулярной перерисовки компонента.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(GameController.this::repaint);
            }
        }, 0, 10);

        // Таймер для обновления состояния модели робота, если она уже установлена.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (robotModel != null) {
                    robotModel.update(targetPositionX, targetPositionY, 10);
                }
            }
        }, 0, 10);

        // Обработка кликов для задания новой целевой позиции.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    /**
     * Задает новую целевую позицию, куда должен двигаться робот.
     */
    public void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }
}
