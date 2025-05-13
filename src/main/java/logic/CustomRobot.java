package logic;
import model.ExternalRobot;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class CustomRobot implements ExternalRobot {
    @Override
    public double getMaxVelocity() { return 0.15; }

    @Override
    public double getMaxAngularVelocity() { return 0.002; }

    @Override
    public void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform oldTransform = g.getTransform();
        g.rotate(direction, x, y);

        // Рисуем корпус (прямоугольник вместо овала)
        g.setColor(new Color(255, 165, 0)); // Оранжевый
        g.fillRect(x - 20, y - 10, 40, 20);

        // Рисуем "глаз" (сенсор)
        g.setColor(Color.RED);
        g.fillOval(x + 15, y - 3, 6, 6);

        // Рисуем колеса
        g.setColor(Color.BLACK);
        g.fillOval(x - 18, y - 8, 6, 6);
        g.fillOval(x - 18, y + 2, 6, 6);
        g.fillOval(x + 12, y - 8, 6, 6);
        g.fillOval(x + 12, y + 2, 6, 6);

        g.setTransform(oldTransform);
    }
}