package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Класс RobotModel представляет модель робота, хранящую его координаты и направление.
 * Использует механизм {@link PropertyChangeSupport} для уведомления подписчиков об изменениях состояния.
 */
public class RobotModel {
    private ExternalRobotModel externalRobotModel;
    private double positionX = 100;
    private double positionY = 100;
    private double direction = 0; // в радианах
    private double targetX = 150;
    private double targetY = 100;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Возвращает текущую позицию робота по оси X.
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Возвращает текущую позицию робота по оси Y.
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Возвращает текущее направление робота (в радианах).
     */
    public double getDirection() {
        return direction;
    }

    /**
     * Возвращает целевую позицию по оси X.
     */
    public double getTargetX() {
        return targetX;
    }

    /**
     * Возвращает целевую позицию по оси Y.
     */
    public double getTargetY() {
        return targetY;
    }

    /**
     * Устанавливает обновленный функционал из jar для робота.
     */
    public void setExternalRobot(ExternalRobotModel externalRobotModel) {
        this.externalRobotModel = externalRobotModel;
    }

    /**
     * Устанавливает целевую позицию для робота.
     * Уведомляет подписчиков об изменении целевых координат.
     */
    public void setTargetPosition(double x, double y) {
        double oldX = targetX;
        double oldY = targetY;
        targetX = x;
        targetY = y;
        pcs.firePropertyChange("targetX", oldX, targetX);
        pcs.firePropertyChange("targetY", oldY, targetY);
    }

    /**
     * Обновляет состояние робота за определенный промежуток времени.
     * Вычисляет новое положение и направление робота.
     */
    public void update(double duration) {
        double distance = Math.hypot(targetX - positionX, targetY - positionY);
        if (distance < 0.5) return;

        final double maxVelocity = externalRobotModel != null ?
                externalRobotModel.getMaxVelocity() : 0.1;
        final double maxAngularVelocity = externalRobotModel != null ?
                externalRobotModel.getMaxAngularVelocity() : 0.001;
        double angleToTarget = Math.atan2(targetY - positionY, targetX - positionX);
        angleToTarget = normalizeAngle(angleToTarget);

        double angleDiff = normalizeAngle(angleToTarget - direction);
        double angularVelocity;

        if (angleDiff > Math.PI) {
            angularVelocity = -maxAngularVelocity;
        } else if (angleDiff < -Math.PI) {
            angularVelocity = maxAngularVelocity;
        } else {
            angularVelocity = (angleDiff > 0) ? maxAngularVelocity : -maxAngularVelocity;
        }
        double newX = positionX + maxVelocity * duration * Math.cos(direction);
        double newY = positionY + maxVelocity * duration * Math.sin(direction);

        RobotsPosition oldPos = new RobotsPosition(positionX, positionY);

        positionX = newX;
        positionY = newY;
        direction = normalizeAngle(direction + angularVelocity * duration);

        RobotsPosition newPos = new RobotsPosition(positionX, positionY);
        firePositionChange(oldPos, newPos);
    }

    private void firePositionChange(RobotsPosition oldPos, RobotsPosition newPos) {
        pcs.firePropertyChange("position", oldPos, newPos);
    }

    /**
     * Добавляет слушателя для отслеживания изменений в модели робота.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Нормализует угол, приводя его в диапазон [0, 2π).
     */
    private double normalizeAngle(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
