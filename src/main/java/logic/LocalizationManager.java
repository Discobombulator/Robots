package logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Singleton-класс для управления локализацией приложения.
 * Позволяет изменять текущую локаль и оповещать слушателей об изменении языка.
 */
public class LocalizationManager {
    private static final LocalizationManager instance = new LocalizationManager();
    private Locale currentLocale = Locale.of("ru");
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Приватный конструктор для реализации шаблона Singleton.
     */
    private LocalizationManager() {
    }

    /**
     * Возвращает единственный экземпляр
     */
    public static LocalizationManager getInstance() {
        return instance;
    }

    /**
     * Добавляет слушателя изменений локали.
     *
     * @param listener объект, реализующий {@code PropertyChangeListener}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Устанавливает новую локаль и оповещает всех слушателей.
     *
     * @param locale новая локаль
     */
    public void setLocale(Locale locale) {
        Locale oldLocale = currentLocale;
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        support.firePropertyChange("locale", oldLocale, locale);
    }

    /**
     * Возвращает локализованную строку по ключу.
     */
    public String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * Возвращает текущую локаль.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
