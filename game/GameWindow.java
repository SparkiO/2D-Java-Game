package game;

import javax.swing.*;
import java.awt.*;

/**
 * Full Game window that holds the main game.
 */
public class GameWindow extends JFrame {
    public static GraphicsEnvironment env; //user's graphical environment
    public static GraphicsDevice device; //main screen device in the environment
    public static Rectangle RECTANGLE; //boundaries of the screen device
    public static int WIDTH;
    public static int HEIGHT;

    /**
     * Gets user's screen devices boundaries so the game can be run in Full Screen mode.
     */
    static {
        try {
            env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = env.getScreenDevices()[0];
            RECTANGLE = device.getDefaultConfiguration().getBounds();
        } catch (Exception e) {
            RECTANGLE = new Rectangle(1920, 1080);
        } finally {
            WIDTH = RECTANGLE.width;
            HEIGHT = RECTANGLE.height;
        }
    }

    public Component comp; //component to be painted on the window

    public GameWindow(Component comp) {
        super();
        this.comp = comp;
        getContentPane().add(BorderLayout.CENTER, comp);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        repaint();
    }
}
