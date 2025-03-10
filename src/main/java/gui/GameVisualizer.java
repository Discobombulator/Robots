package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 * Класс GameVisualizer отвечает за отрисовку игрового поля, включая
 * визуализацию робота и целевой точки. Обновление состояния модели робота
 * осуществляется посредством таймеров и подписки на изменения модели.
 */
public class GameVisualizer extends JPanel {
    private final Timer timer = new Timer("events generator", true);
    private RobotModel robotModel;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    /**
     * Конструктор GameVisualizer. Инициализирует таймеры для перерисовки и обновления модели,
     * а также обрабатывает события кликов для задания новой целевой позиции.
     */
    public GameVisualizer() {
        // Таймер для регулярной перерисовки компонента.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(GameVisualizer.this::repaint);
            }
        }, 0, 50);

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
     * Устанавливает общий экземпляр модели робота, который будет использоваться
     * для обновления и отрисовки.
     */
    public void setRobotModel(RobotModel model) {
        this.robotModel = model;
        // Подписываемся на изменения модели для вызова перерисовки.
        robotModel.addPropertyChangeListener(evt -> repaint());
    }

    /**
     * Задает новую целевую позицию, куда должен двигаться робот.
     */
    private void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }

    /**
     * Переопределённый метод отрисовки компонента. Выполняет отрисовку робота и целевой точки.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (robotModel != null) {
            int robotX = (int) (robotModel.getPositionX() + 0.5);
            int robotY = (int) (robotModel.getPositionY() + 0.5);
            double robotDirection = robotModel.getDirection();
            drawRobot(g2d, robotX, robotY, robotDirection);
        }
        drawTarget(g2d, targetPositionX, targetPositionY);
    }

    /**
     * Заполняет овал с указанными параметрами.
     */
    private void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует овал с указанными параметрами.
     */
    private void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Отрисовывает робота с заданными координатами и направлением.
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform oldTransform = g.getTransform();
        AffineTransform transform = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(transform);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
        g.setTransform(oldTransform);
    }

    /**
     * Отрисовывает целевую точку.
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
