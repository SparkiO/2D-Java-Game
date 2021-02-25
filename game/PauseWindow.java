package game;

import game.controller.Action;

import javax.swing.*;
import java.awt.*;

public class PauseWindow extends JFrame {
    private JButton[] buttons = new JButton[4];
    private String[] labels = {"Resume", "Save", "Load", "Exit"};
    private JPanel centrePanel;
    private Font font = new Font("TimesRoman", Font.PLAIN, 20);
    private game.controller.Action action;
    ;

    public PauseWindow(Action action) {
        this.action = action;
        setPreferredSize(new Dimension(400, 500));
        setUndecorated(true);
        setResizable(false);
        setTitle("PauseWindow");

        /**
         * initializing components for the frame
         */
        centrePanel = new JPanel();
        BoxLayout boxlayout = new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS);
        centrePanel.setLayout(boxlayout);
        centrePanel.setFont(font);
        centrePanel.setForeground(Color.WHITE);
        centrePanel.setBorder(BorderFactory.createTitledBorder(null, "Pause Window", 2, 2, font, Color.WHITE));
        centrePanel.setBackground(Color.BLACK);

        setElements();
        getContentPane().add(centrePanel);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void setElements() {
        for (int i = 0; i < buttons.length; i++) {
            centrePanel.add(Box.createRigidArea(new Dimension(0, 30)));
            buttons[i] = new JButton(labels[i]);
            buttons[i].setFont(font);
            buttons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setBackground(Color.WHITE);
            centrePanel.add(buttons[i]);
        }
        setButtonsAction();
    }

    private void setButtonsAction() {
        buttons[0].addActionListener(e -> {
            action.pause = false;
            setVisible(false);
        });

        buttons[1].addActionListener(e -> {
            action.save = true;
        });

        buttons[2].addActionListener(e -> {
            action.load = true;
            setVisible(false);
        });

        buttons[3].addActionListener(e -> {
            System.exit(0);
        });
    }
}
