package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

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
public class LogWindow extends JInternalFrame implements LogChangeListener {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
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
        super("Протокол работы", true, true, true, true);
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
            content.append(entry.getMessage()).append("\n");
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
}
