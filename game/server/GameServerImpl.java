package game.server;

import game.*;
import game.controller.Action;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;

/**
 * Remote object representing the game server. Methods are to be invoked by a remote Java Virtual Machine using RMI in RemoteGameClient.
 */
public class GameServerImpl extends UnicastRemoteObject implements GameServer {
    private Game game; // current instance of the game

    public GameServerImpl() throws RemoteException {
        super(Constants.SERVER_PORT);
    }

    /**
     * Starts a new game on the server.
     * @param difficulty object representing the difficulty (number of asteroids and number of enemies) of the game to be started
     */
    @Override
    public void newGame(Difficulty difficulty) throws RemoteException {
        ScoreTracker scoreTracker = new ScoreTracker(difficulty.getRoundTime());
        game = new Game(scoreTracker);
        game.init();
        new Thread(game).start();
        System.out.println("The game has started!");
    }

    /**
     * Connects a new player (client) to the existing game.
     * @param playerName nickname of the player to be connected
     * @return Player object representing the new player connected to the game
     */
    @Override
    public Player connect(String playerName) throws RemoteException {
        if (game == null)
            newGame(Constants.DEFAULT_DIFFICULTY);
        return game.newPlayer(playerName);
    }

    /**
     * Updates the game with an action made by the player (client).
     * @param playerId id of the player connected to the game
     * @param action action of the player
     * @return updated Game instance
     */
    @Override
    public Game update(int playerId, Action action) throws RemoteException {
        return game.doAction(playerId, action);
    }

    /**
     * Main method to be invoked by the remote server.
     */
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", Constants.SERVER_IP); // set virtual machine variable to server IP
            GameServer gs = new GameServerImpl();
            LocateRegistry.createRegistry(Constants.SERVER_PORT); // create RMI registry
            Naming.rebind(Constants.REMOTE_SERVER_NAME, gs); // bind the server remote object to a name so it can be looked up by the client
            System.out.println("The server has started!");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
