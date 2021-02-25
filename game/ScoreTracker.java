package game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Score class which tracks the current score of the user.
 */
public class ScoreTracker implements Serializable {

    private Map<String, Integer> scores = new HashMap<>(); //holds name of the players and their scores
    private int enemiesLeft = 0; //current enemies left in the game
    private int roundTime; //time of the round
    private long startTime; //time of the start of the game
    private long pausedTime; //time when the game has been paused
    private long timeLeft; //time left to the end of the game

    public enum ScoreType {ASTEROID, ENEMY} //type of score

    public ScoreTracker(int roundTime) {
        this.roundTime = roundTime;
    }

    /**
     * Gets the score from the Map and returns it. If not existing yet - returns 0.
     * @param playerName name of the player
     * @return int which is equal to the player's score
     */
    public int getScore(String playerName) {
        Integer score = scores.get(playerName);
        return score == null ? 0 : score;
    }

    /**
     * Increases score depending on the type.
     * @param S type of the score (asteroid/enemy)
     * @param playerName name of the player
     */
    public void incScore(ScoreType S, String playerName) {
        int score = getScore(playerName);
        switch (S) {
            case ASTEROID:
                scores.put(playerName, score + 50);
                break;
            case ENEMY:
                scores.put(playerName, score + 100);
                break;
        }
    }

    public int getEnemiesLeft() {
        return enemiesLeft;
    }

    public void incEnemiesLeft(int i) {
        enemiesLeft += i;
    }

    public void decEnemiesLeft() {
        enemiesLeft--;
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void timer() {
        timeLeft = roundTime - (System.currentTimeMillis() - startTime) / 1000;
    }

    public void pausedTime() {
        pausedTime = System.currentTimeMillis();
    }

    public void restoreStartTime() {
        startTime += System.currentTimeMillis() - pausedTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }
}
