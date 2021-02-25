package game.server;

import game.controller.Action;
import game.Difficulty;
import game.Game;
import game.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface representing the game server. Methods are to be invoked by a remote Java Virtual Machine using RMI in RemoteGameClient.
 */
public interface GameServer extends Remote {

    /**
     * Starts a new game on the server.
     * @param difficulty object representing the difficulty (number of asteroids and number of enemies) of the game to be started
     */
    void newGame(Difficulty difficulty) throws RemoteException;

    /**
     * Connects a new player (client) to the existing game.
     * @param playerName nickname of the player to be connected
     * @return Player object representing the new player connected to the game
     */
    Player connect(String playerName) throws RemoteException;

    /**
     * Updates the game with an action made by the player (client).
     * @param playerId id of the player connected to the game
     * @param action action of the player
     * @return updated game instance
     */
    Game update(int playerId, Action action) throws RemoteException;
}
