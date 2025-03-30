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
    private final GameController controller;

    /**
     * Конструктор GameVisualizer. Инициализирует таймеры для перерисовки и обновления модели,
     * а также обрабатывает события кликов для задания новой целевой позиции.
     */
    public GameVisualizer(GameController controller) {
        this.controller = controller;
        // Обработчик кликов остаётся, если он нужен
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.setTargetPosition(e.getPoint());
                repaint();
            }
        });
        // Таймер для регулярной перерисовки
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(() -> repaint());
            }
        }, 0, 10); // обновление каждые 50 мс
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (controller.getRobotModel() != null) {
            int robotX = (int) (controller.getRobotModel().getPositionX() + 0.5);
            int robotY = (int) (controller.getRobotModel().getPositionY() + 0.5);
            double robotDirection = controller.getRobotModel().getDirection();
            drawRobot(g2d, robotX, robotY, robotDirection);
        }
        drawTarget(g2d, controller.getTargetPositionX(), controller.getTargetPositionY());
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
