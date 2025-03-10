package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

/**
 * Главное окно приложения.
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowsSaver windowsSaver = new WindowsSaver();
    private final GameWindow gameWindow = new GameWindow();
    private final LogWindow logWindow = createLogWindow();

    public MainApplicationFrame() throws IOException, PropertyVetoException {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        // Загружаем данные о позициях окон
        windowsSaver.loadFromFile(gameWindow, logWindow);

        // Добавляем окна на рабочий стол
        addWindow(logWindow);
        addWindow(gameWindow);

        SwingUtilities.invokeLater(() -> {
            try {
                gameWindow.setIcon(gameWindow.getGameData()[4] == 0);
                logWindow.setIcon(logWindow.getLogData()[4] == 0);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        });

        setJMenuBar(generateMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    confirmExit();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Генерирует панель меню.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> {
            try {
                confirmExit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    /**
     * Создает меню "Режим отображения".
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");
        lookAndFeelMenu.add(createLookMenu());
        lookAndFeelMenu.add(createFeelMenu());
        return lookAndFeelMenu;
    }

    /**
     * Вызывает диалог подтверждения выхода, сохраняет состояние окон и завершает работу приложения.
     *
     * @throws IOException если возникает ошибка ввода-вывода при сохранении состояния
     */
    private void confirmExit() throws IOException {
        int confirmed = JOptionPane.showConfirmDialog(
                MainApplicationFrame.this,
                "Выходим?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            gameWindow.setGameData(windowsSaver.saveWidowData(gameWindow));
            logWindow.setLogData(windowsSaver.saveWidowData(logWindow));
            windowsSaver.saveToFile(gameWindow, logWindow);

            dispose();
            System.exit(0);
        }
    }

    /**
     * Создает элемент меню для системной схемы.
     */
    private JMenuItem createLookMenu() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    /**
     * Создает элемент меню для универсальной схемы.
     */
    private JMenuItem createFeelMenu() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    /**
     * Создает меню "Тесты".
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);
        return testMenu;
    }

    /**
     * Устанавливает внешний вид приложения.
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
