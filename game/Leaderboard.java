package game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Leaderboard class that represents the board showing players' scores.
 */
public class Leaderboard {
    File leaderboardFile; //file holding score information
    LeaderboardWindow leaderboardWindow; //JFrame window showing the score

    /**
     * Constructor that creates new Window and File objects.
     * If file doesn't exist yet on the given path - create new one.
     * @throws IOException input/output exception
     */
    public Leaderboard() throws IOException {
        leaderboardWindow = new LeaderboardWindow();
        leaderboardFile = new File(Constants.SCORES_FILE);
        if (!leaderboardFile.exists())
            createNew();
    }

    /**
     * Shows the leaderboard window.
     */
    public void show() throws IOException {
        leaderboardWindow.updateLeaderboardTable(getLeaderboard());
        leaderboardWindow.setVisible(true);
    }

    /**
     * Saves the score of player to the File separated by sing '|'.
     * @param score int of score
     * @param playerName name of the player
     */
    public void saveScore(int score, String playerName) throws IOException {
        try (FileWriter fw = new FileWriter(leaderboardFile, true)) {
            fw.append(String.format("%d|%s\n", score, playerName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the File holding users' scores and their names.
     * @return the map of Scores and Player names
     */
    public Map<Integer, String> getLeaderboard() throws IOException {
        Map<Integer, String> leaderboard = new TreeMap<>(Collections.reverseOrder()); //treemap with descending scores

        Scanner s = new Scanner(leaderboardFile);
        s.useDelimiter("[\\s|]");
        while (s.hasNext())
            leaderboard.put(s.nextInt(), s.next());

        s.close();
        return leaderboard;
    }

    /**
     * Clears the File with scores.
     */
    public void clear() throws IOException {
        FileWriter writer = new FileWriter(leaderboardFile);
        writer.write("");
        writer.close();
        createNew();
    }

    /**
     * Creates new File with scores.
     */
    private void createNew() throws IOException {
        leaderboardFile.createNewFile();
    }

    /**
     * LeaderboardWindow that inherits JFrame - shows the Score in the game.
     */
    public class LeaderboardWindow extends JFrame {
        private JButton[] buttons; //buttons on the frame
        private JPanel centrePanel; //centrePanel which holds the leaderboard
        private JTable leaderboardTable; //table with leaderboard information
        private Font font = new Font(Font.SERIF, Font.PLAIN, 20); //font used on the frame

        /**
         * Creates the Window, sets the Fonts and foregrounds/background.
         */
        public LeaderboardWindow() {
            super("Leaderboard");

            setResizable(false);

            centrePanel = new JPanel();
            BoxLayout boxlayout = new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS);
            centrePanel.setLayout(boxlayout);
            centrePanel.setFont(font);
            centrePanel.setForeground(Color.WHITE);
            centrePanel.setBorder(BorderFactory.createTitledBorder(null, "Leaderboard", 2, 2, font, Color.WHITE));
            centrePanel.setBackground(Color.BLACK);
            buttons = new JButton[2];

            setElements();
            setButtonsAction();

            getContentPane().add(centrePanel);
            pack();
            setLocationRelativeTo(null);
        }

        /**
         * Sets elements on the Frame in proper position.
         */
        private void setElements() {
            DefaultTableModel model = new DefaultTableModel();
            leaderboardTable = new JTable(model);
            leaderboardTable.setDefaultEditor(Object.class, null);
            leaderboardTable.setRowHeight(25);
            leaderboardTable.setRowSelectionAllowed(false);

            //adds columns of position, nickname and score
            for (String columnName : new String[]{"Position", "Nickname", "Score"})
                model.addColumn(columnName);

            leaderboardTable.setPreferredSize(new Dimension(300, 400));
            leaderboardTable.setVisible(true);

            JPanel tablePanel = new JPanel();
            tablePanel.setPreferredSize(new Dimension(300, 400));
            tablePanel.add(leaderboardTable);

            centrePanel.add(tablePanel, BorderLayout.NORTH);
            buttons[0] = new JButton("Clear leaderboard");

            centrePanel.add(buttons[0]);
        }

        /**
         * Set the button's action to clear the leaderboard and the file when pressed.
         */
        private void setButtonsAction() {
            buttons[0].addActionListener(e -> {
                try {
                    clear();
                    clearLeaderboardTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error has occurred while accessing the leaderboard file.");
                }
            });
        }

        /**
         * Updates the leaderboard table by adding the rows of data from the File.
         */
        public void updateLeaderboardTable(Map<Integer, String> leaderboard) {
            DefaultTableModel model = (DefaultTableModel) leaderboardTable.getModel();
            clearLeaderboardTable();

            int position = 1;
            for (Map.Entry<Integer, String> entry : leaderboard.entrySet()) {
                String[] data = new String[]{String.valueOf(position++), entry.getValue(), String.valueOf(entry.getKey())};
                model.addRow(data);
            }
        }

        /**
         * Clears the table on the GUI but doesn't clear the File.
         */
        public void clearLeaderboardTable() {
            ((DefaultTableModel) leaderboardTable.getModel()).setRowCount(0);
        }
    }
}
