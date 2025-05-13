package model;

import java.awt.*;

/**
 * Интерфейс для внешних реализаций роботов
 */
public interface ExternalRobot {
    double getMaxVelocity();
    double getMaxAngularVelocity();
    void drawRobot(Graphics2D g, int x, int y, double direction);
}