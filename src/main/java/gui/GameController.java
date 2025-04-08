package gui;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер игры, который управляет обновлением состояния модели робота.
 * Использует таймер для периодического обновления положения робота.
 */
public class GameController {
    private final RobotModel model;

    /**
     * Создает контроллер игры и запускает таймер для обновления состояния робота каждые 10 мс.
     */
    public GameController(RobotModel model) {
        this.model = model;


        // Периодическое обновление
        Timer timer = new Timer("RobotUpdateTimer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.update(10);
            }
        }, 0, 10);
    }

    /**
     * Обрабатывает пользовательский клик по игровому полю.
     * Устанавливает целевую позицию для робота.
     */
    public void onUserClick(int x, int y) {
        model.setTargetPosition(x, y);
    }
}

