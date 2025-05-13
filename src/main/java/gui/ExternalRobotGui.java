package gui;

import java.awt.Graphics2D;

/**
 * Интерфейс для графического представления робота
 */
public interface ExternalRobotGui {
    /**
     * Отрисовывает робота на поле
     */
    void drawRobot(Graphics2D g, int x, int y, double direction);

}
