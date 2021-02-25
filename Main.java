import game.Difficulty;
import game.Leaderboard;
import game.client.GameClient;
import game.client.LocalGameClient;
import game.client.RemoteGameClient;
import game.Constants;
import game.server.GameServer;
import utilities.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main point of entry to the game. Show the welcome window where user can specify the settings and choose game mode.
 */
public class Main {
    //components of the welcome window
    private final JPanel panel;
    private final String[] options;
    private final JTextField username;
    private final JSpinner asteroidsSpinner;
    private final JSpinner enemiesSpinner;
    private final JSpinner timeSpinner;

    public Main() {
        options = new String[]{"Leaderboard", "Singleplayer", "Multiplayer"};

        panel = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("Welcome to Asteroids!");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title);

        JPanel settings = new JPanel(new GridLayout(4, 2, 10, 10));

        settings.add(new JLabel("Nickname:"));

        username = new JTextField(20);
        settings.add(username);

        settings.add(new JLabel("Round time:"));
        timeSpinner = new JSpinner(new SpinnerNumberModel(60, 20, 200, 1));
        settings.add(timeSpinner);

        settings.add(new JLabel("Number of asteroids:"));
        asteroidsSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 50, 1));
        settings.add(asteroidsSpinner);

        settings.add(new JLabel("Number of enemies:"));
        enemiesSpinner = new JSpinner(new SpinnerNumberModel(10, 3, 20, 1));
        settings.add(enemiesSpinner);

        panel.add(settings);
    }

    /**
     * Displays the welcome window where an user can specify settings and game mode.
     * @return user choice of game mode (1 for singleplayer, 2 for multiplayer) or show leaderboard choice (0)
     */
    public int display() {
        return JOptionPane.showOptionDialog(null, panel, "Asteroids", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    }


    /**
     * Getters for user settings input.
     */

    public String getName() {
        return username.getText();
    }

    public int getRoundTime() {
        return (int) timeSpinner.getValue();
    }

    public int getNumberOfAsteroids() {
        return (int) asteroidsSpinner.getValue();
    }

    public int getNumberOfEnemies() {
        return (int) enemiesSpinner.getValue();
    }

    public static void main(String[] args) {
        GameClient client = null;
        SoundManager.init(); // turn on the sound manager

        Main mainWindow = new Main();

        Leaderboard leaderboard = null;
        try {
            leaderboard = new Leaderboard();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int res;
        String playerName;
        MainLoop:
        while (true) {
            res = mainWindow.display(); // show the main window
            playerName = mainWindow.getName(); // get the player name
            Difficulty difficulty = new Difficulty(mainWindow.getNumberOfAsteroids(), mainWindow.getNumberOfEnemies(), mainWindow.getRoundTime()); // get the difficulty settings

            if ((res == 1 || res == 2) && playerName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please provide your name.");
                continue;
            }

            switch (res) {
                case 0: // show leaderboard
                    try {
                        leaderboard.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error has occurred while accessing the leaderboard file.");
                    }
                    break MainLoop;
                case 1: // assign singleplayer client
                    client = new LocalGameClient(difficulty, leaderboard);
                    break MainLoop;
                case 2: // assign multiplayer client
                    try {
                        Registry registry = LocateRegistry.getRegistry(Constants.SERVER_IP, 1099);
                        GameServer server = (GameServer) registry.lookup(Constants.REMOTE_SERVER_NAME);

                        client = new RemoteGameClient(server, difficulty, leaderboard);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break MainLoop;
                default:
                    break MainLoop;
            }
        }

        if (res != -1 && res != 0) {
            if (client != null && client.init(playerName)) { // initialise the client
                client.run(); // run the client
            } else
                JOptionPane.showMessageDialog(null, "An error has occurred while trying to initialise game.");
        }
    }
}
