package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

/**
 * Класс GameVisualizer отвечает за отрисовку игрового поля, включая
 * визуализацию робота и целевой точки. Обновление состояния модели робота
 * осуществляется посредством подписки на изменения модели.
 */
public class GameVisualizer extends JPanel {
    private final GameController controller;

    /**
     * Создает визуализатор игры и добавляет обработчик кликов для установки целевой точки.
     */
    public GameVisualizer(GameController controller) {
        this.controller = controller;
        controller.getRobotModel().addPropertyChangeListener(evt -> repaint());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller.getRobotModel() != null) {
                    controller.getRobotModel().setTargetPosition(e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Отрисовывает игровое поле, включая робота и целевую точку.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        RobotModel model = controller.getRobotModel();

        if (model != null) {
            drawRobot(g2d, (int) model.getPositionX(), (int) model.getPositionY(), model.getDirection());
            drawTarget(g2d, (int) model.getTargetX(), (int) model.getTargetY());
        }
    }

    /**
     * Отрисовывает робота в заданной позиции и направлении.
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform oldTransform = g.getTransform();
        g.rotate(direction, x, y);
        g.setColor(Color.MAGENTA);
        g.fillOval(x - 15, y - 5, 30, 10);
        g.setColor(Color.BLACK);
        g.drawOval(x - 15, y - 5, 30, 10);
        g.setColor(Color.WHITE);
        g.fillOval(x + 5, y - 2, 5, 5);
        g.setColor(Color.BLACK);
        g.drawOval(x + 5, y - 2, 5, 5);
        g.setTransform(oldTransform);
    }

    /**
     * Отрисовывает целевую точку.
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        g.fillOval(x - 2, y - 2, 5, 5);
        g.setColor(Color.BLACK);
        g.drawOval(x - 2, y - 2, 5, 5);
    }
}
