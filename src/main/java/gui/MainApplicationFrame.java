package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;

import log.Logger;

/**
 * Класс MainApplicationFrame является главным окном приложения.
 * Он содержит рабочий стол (JDesktopPane), на котором располагаются окна игрового поля,
 * протокола работы и окно с информацией о роботе. Также данный класс отвечает за загрузку
 * и сохранение состояния окон, создание меню и обработку выхода из приложения.
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowsSaver windowsSaver = new WindowsSaver();

    // Общий экземпляр модели робота, который используется в RobotInfoWindow и в GameWindow (через GameVisualizer)
    private final RobotModel sharedRobotModel = new RobotModel();

    private final GameWindow gameWindow;
    private final LogWindow logWindow = createLogWindow();
    private final RobotInfoWindow robotInfoWindow;

    /**
     * Конструктор главного окна приложения. Он устанавливает размеры окна, создает и размещает
     * на рабочем столе окна игрового поля, протокола и окна с информацией о роботе, а также загружает
     * их сохраненные состояния.
     * Генерирует исключение PropertyVetoException, если установка свернутости окна не разрешена,
     * или IOException, если возникает ошибка при загрузке состояния.
     */
    public MainApplicationFrame() throws IOException, PropertyVetoException {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);
        gameWindow = new GameWindow(sharedRobotModel);
        robotInfoWindow = new RobotInfoWindow(sharedRobotModel);

        // Загружаем состояния окон, включая MainApplicationFrame
        windowsSaver.loadFromFile(this, gameWindow, logWindow, robotInfoWindow);

        addWindow(logWindow);
        addWindow(gameWindow);
        addWindow(robotInfoWindow);

        SwingUtilities.invokeLater(() -> {
            try {
                gameWindow.setIcon(gameWindow.getGameData()[4] == 0);
                logWindow.setIcon(logWindow.getLogData()[4] == 0);
                robotInfoWindow.setIcon(robotInfoWindow.getWindowData()[4] == 0);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        });


        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addExitButton();

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

    /**
     * Создает окно протокола работы приложения. Окно регистрируется в источнике лог-сообщений.
     * Устанавливаются начальные координаты и размеры окна, после чего окно упаковывается.
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Добавляет переданное окно на рабочий стол приложения и делает его видимым.
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создает строку меню приложения. Меню включает в себя пункты смены режима отображения,
     * тестовые команды и пункт выхода.
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
     * Создает меню для выбора режима отображения приложения. Меню включает пункты для
     * системной и универсальной схемы.
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
     * Создает пункт меню для смены на системную схему отображения. При выборе пункта происходит
     * смена режима отображения окна на системный.
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
     * Создает пункт меню для смены на универсальную схему отображения. При выборе пункта происходит
     * смена режима отображения окна на универсальный.
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
     * Создает меню с тестовыми командами для отладки. В настоящее время включает команду
     * для добавления нового сообщения в лог.
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        // Первая кнопка: добавляет стандартное сообщение
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });

        // Вторая кнопка: добавляет другое сообщение
        JMenuItem addLogErrorItem = new JMenuItem("Ошибка протокола", KeyEvent.VK_P);
        addLogErrorItem.addActionListener((event) -> {
            Logger.debug("Протокол не работает");
        });

        testMenu.add(addLogMessageItem);
        testMenu.add(addLogErrorItem);
        return testMenu;
    }


    /**
     * Добавляет кнопку выхода, которая располагается в нижней части главного окна.
     * При нажатии на кнопку вызывается процедура подтверждения выхода из приложения.
     */
    private void addExitButton() {
        JPanel panel = new JPanel();
        JButton exitButton = new JButton("Выход");
        exitButton.addActionListener(e -> {
            try {
                confirmExit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        panel.add(exitButton);
        add(panel, BorderLayout.SOUTH);
    }

    /**
     * Метод для подтверждения выхода из приложения.
     * При положительном ответе сохраняется состояние всех окон, после чего приложение закрывается.
     * Генерирует IOException при ошибке ввода-вывода.
     */
    private void confirmExit() throws IOException {
        int confirmed = JOptionPane.showConfirmDialog(
                MainApplicationFrame.this,
                "Выходим?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmed == JOptionPane.YES_OPTION) {
            windowsSaver.saveToFile(this, gameWindow, logWindow, robotInfoWindow);
            dispose();
            System.exit(0);
        }
    }

    /**
     * Устанавливает внешний вид приложения, обновляя интерфейс всех компонентов.
     * При возникновении ошибки смены темы ошибка игнорируется.
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Игнорируем ошибки смены темы
        }
    }
}
