package game;

import game.controller.Action;

import javax.swing.*;
import java.awt.*;

/**
 * Controls the in-game menu.
 */
public class Menu extends JDialog {
    private JButton[] buttons;
    private String[] labels;
    private JPanel centrePanel; //the main panel of the window
    private Font font = new Font(Font.SERIF, Font.PLAIN, 20); //font used throughout the window
    private Action action; //action object to handle the user's interaction with buttons

    public enum Configuration {SINGLEPLAYER_LOBBY, SINGLEPLAYER_PAUSE, MULTIPLAYER_LOBBY, MULTIPLAYER_PAUSE} // possible menu configuration constants

    public Menu(JFrame parent, Action action) {
        super(parent, "Menu", false);

        this.action = action;
        setUndecorated(true);
        setResizable(false);

        /**
         * initializing components for the dialog
         */
        centrePanel = new JPanel();
        BoxLayout boxlayout = new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS);
        centrePanel.setLayout(boxlayout);
        centrePanel.setFont(font);
        centrePanel.setForeground(Color.WHITE);
        centrePanel.setBorder(BorderFactory.createTitledBorder(null, "Menu", 2, 2, font, Color.WHITE));
        centrePanel.setBackground(Color.BLACK);

        getContentPane().add(centrePanel);
    }

    /**
     * Creates components depending on the specified configuration.
     */
    public void setElements(Configuration configuration) {
        switch (configuration) {
            case SINGLEPLAYER_LOBBY:
                buttons = new JButton[3];
                labels = new String[]{"New game", "Load", "Exit"};
                setPreferredSize(new Dimension(400, 375));
                break;
            case SINGLEPLAYER_PAUSE:
                buttons = new JButton[5];
                labels = new String[]{"Resume", "New game", "Save", "Load", "Exit"};
                setPreferredSize(new Dimension(400, 600));
                break;
            case MULTIPLAYER_LOBBY:
                buttons = new JButton[3];
                labels = new String[]{"Join", "Start new", "Exit"};
                setPreferredSize(new Dimension(400, 375));
                break;
            case MULTIPLAYER_PAUSE:
                buttons = new JButton[2];
                labels = new String[]{"Resume", "Exit"};
                setPreferredSize(new Dimension(400, 250));
                break;
        }

        centrePanel.removeAll();
        for (int i = 0; i < buttons.length; i++) {
            centrePanel.add(Box.createRigidArea(new Dimension(0, 30)));
            buttons[i] = new JButton(labels[i]);
            buttons[i].setFont(font);
            buttons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setBackground(Color.WHITE);
            centrePanel.add(buttons[i]);
        }

        pack();
        setLocationRelativeTo(null);

        setButtonsAction(configuration);
    }

    /**
     * Sets action listeners to respond on button presses depending on the specified configuration.
     */
    private void setButtonsAction(Configuration configuration) {
        switch (configuration) {
            case SINGLEPLAYER_LOBBY:
                buttons[0].addActionListener(e -> {
                    action.newgame = true;
                    setVisible(false);
                    setElements(Configuration.SINGLEPLAYER_PAUSE);
                });

                buttons[1].addActionListener(e -> {
                    action.load = true;
                    setVisible(false);
                    setElements(Configuration.SINGLEPLAYER_PAUSE);
                });

                buttons[2].addActionListener(e -> action.exit = true);
                break;

            case SINGLEPLAYER_PAUSE:
                buttons[0].addActionListener(e -> {
                    action.pause = false;
                    action.resume = true;
                    setVisible(false);
                });

                buttons[1].addActionListener(e -> {
                    action.newgame = true;
                    setVisible(false);
                });

                buttons[2].addActionListener(e -> action.save = true);

                buttons[3].addActionListener(e -> {
                    action.load = true;
                    setVisible(false);
                });

                buttons[4].addActionListener(e -> action.exit = true);
                break;


            case MULTIPLAYER_LOBBY:
                buttons[0].addActionListener(e -> {
                    action.pause = false;
                    setVisible(false);
                    setElements(Configuration.MULTIPLAYER_PAUSE);
                });

                buttons[1].addActionListener(e -> {
                    action.newgame = true;
                    setVisible(false);
                    setElements(Configuration.MULTIPLAYER_PAUSE);
                });

                buttons[2].addActionListener(e -> action.exit = true);
                break;

            case MULTIPLAYER_PAUSE:
                buttons[0].addActionListener(e -> {
                    action.pause = false;
                    action.resume = true;
                    setVisible(false);
                });

                buttons[1].addActionListener(e -> action.exit = true);
                break;
        }

    }
}
