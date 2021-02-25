package game.client;

import game.*;
import game.controller.Keys;

/**
 * Abstract GameClient class used in implementation of Local
 * and Remote GameClients depending on the version of the game (multi vs singleplayer).
 */
public abstract class GameClient implements Runnable {
    protected Game game; //current game
    protected View view; //view component handling the rendering
    protected Menu menu; //menu frame
    protected Keys keys; //keys controller used by player
    protected Player player; //current player
    protected Difficulty difficulty; //difficulty of the game
    protected Leaderboard leaderboard; //leaderboards of the game

    public GameClient(Difficulty difficulty, Leaderboard leaderboard) {
        this.difficulty = difficulty;
        this.leaderboard = leaderboard;
    }

    /**
     * Initialises the game, view and key components required to play.
     * @param playerName nickname of the player to be added
     * @return true if the game has successfully started
     */
    public abstract boolean init(String playerName);
}
