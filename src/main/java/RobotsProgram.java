import gui.MainApplicationFrame;

import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame;
            try {
                frame = new MainApplicationFrame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
            frame.setVisible(true);
        });
    }
}
