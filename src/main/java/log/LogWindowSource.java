package log;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 1. Этот класс больше не порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит сообщения лога, которые выполняют требования,
 *  1) имеют ограниченный размер (старые записи вытесняются)
 *  2) должна быть потокобезопасной (запись и чтение порождают состояние гонки)
 *  3) должна быть возможность доступа к части данных (сегмент смежных записей)
 * по индексам начала и конца (такая операция, по идее, нужна для
 * эффективного отображения данных в окне, чтобы не читать полный лог)
 */
public class LogWindowSource
{

    private final CircleBuffer<LogEntry> m_messages;
    private final Set<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength)
    {
        this.m_messages = new CircleBuffer<>(iQueueLength);
        m_listeners = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    activeListeners = m_listeners.toArray(new LogChangeListener [0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(startFrom + count, m_messages.size());
        return m_messages.getRange(startFrom, toIndex);
    }

    public Iterable<LogEntry> all() {
        return m_messages;
    }
}
