package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Класс RobotModel представляет модель робота, хранящую его координаты и направление.
 * Использует механизм {@link PropertyChangeSupport} для уведомления подписчиков об изменениях состояния.
 */
public class RobotModel {
    private double positionX = 100;
    private double positionY = 100;
    private double direction = 0; // в радианах

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Возвращает текущую координату X робота.
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Возвращает текущую координату Y робота.
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Возвращает текущее направление робота в радианах.
     */
    public double getDirection() {
        return direction;
    }

    /**
     * Добавляет слушателя изменений свойств модели.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Обновляет состояние модели робота на основе целевых координат и времени.
     * Вычисляет новую позицию и направление с учетом ограничений по скорости и угловому вращению.
     * Оповещает слушателей об изменениях координат и направления.
     */
    public void update(double targetX, double targetY, double duration) {
        double distance = Math.hypot(targetX - positionX, targetY - positionY);
        if (distance < 0.5) return;

        final double maxVelocity = 0.1;
        final double maxAngularVelocity = 0.001;
        double angleToTarget = Math.atan2(targetY - positionY, targetX - positionX);
        angleToTarget = normalizeAngle(angleToTarget);

        // Вычисляет разницу углов и корректирует направление
        double angleDiff = normalizeAngle(angleToTarget - direction);
        double angularVelocity = 0;
        if (angleDiff > Math.PI) {
            angularVelocity = maxAngularVelocity;
        }
        if (angleDiff < Math.PI) {
            angularVelocity = -maxAngularVelocity;
        }
        if (angleDiff > 0) {
            angularVelocity = Math.min(maxAngularVelocity, angleDiff / duration);
        } else if (angleDiff < 0) {
            angularVelocity = Math.max(-maxAngularVelocity, angleDiff / duration);
        }

        // Обновление позиции с учетом криволинейного движения
        double newX, newY;
        if (Math.abs(angularVelocity) < 1e-6) {
            // Прямолинейное движение
            newX = positionX + maxVelocity * duration * Math.cos(direction);
            newY = positionY + maxVelocity * duration * Math.sin(direction);
        } else {
            newX = positionX + maxVelocity / angularVelocity *
                    (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
            newY = positionY - maxVelocity / angularVelocity *
                    (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        }

        double oldX = positionX;
        double oldY = positionY;
        double oldDir = direction;

        positionX = newX;
        positionY = newY;
        direction = normalizeAngle(direction + angularVelocity * duration);

        // Оповещает слушателей об изменении координат и направления
        pcs.firePropertyChange("positionX", oldX, positionX);
        pcs.firePropertyChange("positionY", oldY, positionY);
        pcs.firePropertyChange("direction", oldDir, direction);
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
