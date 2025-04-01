package gui;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер игры, который управляет обновлением состояния модели робота.
 * Использует таймер для периодического обновления положения робота.
 */
public class GameController {
    private RobotModel robotModel;
    /**
     * Устанавливает модель робота, которая будет обновляться контроллером.
     */
    public void setRobotModel(RobotModel model) {
        this.robotModel = model;
    }
    /**
     * Возвращает текущую модель робота.
     *
     */
    public RobotModel getRobotModel() {
        return robotModel;
    }
    /**
     * Создает контроллер игры и запускает таймер для обновления состояния робота каждые 10 мс.
     */
    public GameController() {
        Timer timer = new Timer("events generator", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (robotModel != null) {
                    robotModel.update(10);
                }
            }
        }, 0, 10);


    }
}

