package util;

import gui.LoginGUI;
import javax.swing.JFrame;
import java.awt.*;

public class Logout {

    public static void logout(JFrame currentWindow) {
        for (Window window : Window.getWindows()) {
            window.dispose();
        }
        new LoginGUI().setVisible(true);
    }
}

