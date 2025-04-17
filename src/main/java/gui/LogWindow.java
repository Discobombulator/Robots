package gui;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

/**
 * Окно протокола работы приложения.
 * Класс LogWindow отображает лог-сообщения из LogWindowSource
 * и обновляется при изменении лога.
 */
public class LogWindow extends JInternalFrame implements LogChangeListener, PropertyChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;
    /**
     * Массив, содержащий данные о положении, размере и состоянии окна.
     */
    private int[] logData;

    /**
     * Устанавливает данные для окна протокола.
     *
     * @param gameWidth массив данных о состоянии окна
     */
    public void setLogData(int[] gameWidth) {
        this.logData = gameWidth;
    }

    /**
     * Возвращает массив данных о состоянии окна протокола.
     *
     * @return массив данных (координаты, размеры, состояние окна)
     */
    public int[] getLogData() {
        return logData;
    }

    /**
     * Конструктор окна протокола.
     *
     * @param logSource источник лог-сообщений
     */
    public LogWindow(LogWindowSource logSource) {

        super(LocalizationManager.getInstance().getString("log.title")
                , true, true, true, true);

        addPropertyChangeListener(this);

        // Подписываемся на смену локали
        LocalizationManager.getInstance().addPropertyChangeListener(this);

        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(440, 440);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    /**
     * Обновляет содержимое лог-окна.
     * Формирует строку из всех лог-записей и устанавливает её в компонент m_logContent.
     */
    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            if (entry != null) {
                content.append(entry.getMessage()).append("\n");
            }
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    /**
     * Обработчик изменения логов.
     * Вызывает обновление содержимого лог-окна в потоке диспетчеризации событий.
     */
    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            // Обновляем заголовок окна
            setTitle(LocalizationManager.getInstance().getString("game.title"));
        }
    }
}
