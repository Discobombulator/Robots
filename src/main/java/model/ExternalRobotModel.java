package model;

/**
 * Интерфейс для внешних реализаций роботов
 */
public interface ExternalRobotModel {
    /**
     * Возвращает максимальная скорость движения робота
     */
    double getMaxVelocity();
    /**
     * Возвращает максимальный угол движения робота
     */
    double getMaxAngularVelocity();
}