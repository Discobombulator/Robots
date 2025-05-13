package gui;

import controller.GameController;
import model.RobotModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * Класс GameVisualizer отвечает за отрисовку игрового поля, включая
 * визуализацию робота и целевой точки. Обновление состояния модели робота
 * осуществляется посредством подписки на изменения модели.
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private final GameController controller;
    private final RobotModel model;
    private ExternalRobotGui externalRobotGui;

    /**
     * Устанавливает обновленный функционал из jar для робота.
     */
    public void setExternalRobot(ExternalRobotGui externalRobotGui) {
        this.externalRobotGui = externalRobotGui;
    }

    /**
     * Создает визуализатор игры и добавляет обработчик кликов для установки целевой точки.
     */
    public GameVisualizer(RobotModel model) {
        this.model = model;
        this.controller = new GameController(model); // создаем контроллер тут

        // Подписка на изменения модели
        model.addPropertyChangeListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.onUserClick(e.getX(), e.getY());
            }
        });
    }

    /**
     * Отрисовывает игровое поле, включая робота и целевую точку.
     * Вызывается автоматически при необходимости перерисовки компонента.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, (int) model.getPositionX(), (int) model.getPositionY(), model.getDirection());
        drawTarget(g2d, (int) model.getTargetX(), (int) model.getTargetY());
    }


    /**
     * Отрисовывает робота в заданной позиции и направлении.
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        if (externalRobotGui != null) {
            externalRobotGui.drawRobot(g, x, y, direction);
        } else {
            // Стандартная отрисовка
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

    /**
     * Обрабатывает событие изменения свойств модели робота.
     * При возникновении события перерисовывает игровое поле.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}
