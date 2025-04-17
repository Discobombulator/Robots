package model;

/**
 * Класс RobotsPosition представляет координаты позиции робота на игровом поле.
 * Используется для отслеживания позиций робота.
 */
public class RobotsPosition {
    public final double x;
    public final double y;

    /**
     * Создаёт объект позиции робота с заданными координатами.
     */
    public RobotsPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
