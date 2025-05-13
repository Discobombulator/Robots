package logic;

import model.ExternalRobot;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Класс для загрузки внешних реализаций роботов из JAR-файлов
 */
public class RobotLoader {

    /**
     * Загружает класс робота из указанного JAR-файла
     * @param jarFile файл JAR с реализацией робота
     * @param className полное имя класса (с пакетом) для загрузки
     * @return экземпляр класса, реализующего ExternalRobot
     * @throws Exception если произошла ошибка загрузки
     */
    public ExternalRobot loadRobotFromJar(File jarFile, String className) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        Class<?> robotClass = classLoader.loadClass(className);
        return (ExternalRobot) robotClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Создает и настраивает JFileChooser для выбора JAR-файлов
     * @param title заголовок диалогового окна
     * @return настроенный JFileChooser
     */
    public JFileChooser createJarFileChooser(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
            }

            @Override
            public String getDescription() {
                return "JAR files (*.jar)";
            }
        });
        return fileChooser;
    }
}
