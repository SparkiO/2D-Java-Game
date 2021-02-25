package game.client;

import game.*;
import game.controller.Action;
import game.controller.Keys;
import game.model.GameObject;
import game.server.GameServer;
import game.GameWindow;

import java.rmi.RemoteException;

/**
 * Remote Client used in multiplayer version of the game.
 */
public class RemoteGameClient extends GameClient {
    private GameServer server; // reference to the remote server

    public RemoteGameClient(GameServer server, Difficulty difficulty, Leaderboard leaderboard) throws RemoteException {
        super(difficulty, leaderboard);
        this.server = server;
    }

    /**
     * Initialises the game, view and key components required to play.
     * @param playerName nickname of the player to be added
     * @return true if the game has successfully started
     */
    @Override
    public boolean init(String playerName) {
        try {
            player = server.connect(playerName); // connect to the remote server
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (player != null) {
            keys = new Keys();
            view = new View(execute(new Action()));
            view.setPlayer(player);

            GameWindow gw = new GameWindow(view);
            gw.addKeyListener(keys);
            menu = new Menu(gw, keys.action());
            menu.setElements(Menu.Configuration.MULTIPLAYER_LOBBY);
            menu.setVisible(true);
            return true;
        } else
            return false;
    }

    /**
     * Updates the server with player's action. Loads all game objects after deserialisation of the Game returned from the server.
     * @param action Action performed by the player
     * @return updated Game instance
     */
    private Game execute(Action action) {
        try {
            Game game = server.update(player.getId(), action);

            for (GameObject obj : game.objects)
                obj.load();

            return game;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Client loop handling actions stored in Action object and passing them to the Game. Calls View repaint.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Action action = keys.action();

                game = execute(action); // send action to the server and get back an updated Game
                player = game.getPlayers()[player.getId()];

                view.setGame(game);
                view.setPlayer(player);

                if (action.pause) {
                    menu.setVisible(true);
                    action.pause = false;
                }
                if (action.resume) {
                    menu.setVisible(false);
                    action.resume = false;
                }
                if (action.newgame) {
                    server.newGame(difficulty);
                    player = server.connect(player.getName());
                    action.newgame = false;
                }
                if (action.exit) {
                    String playerName = player.getName();
                    int score = game.getScoreTracker().getScore(playerName);
                    if (score > 0)
                        leaderboard.saveScore(score, playerName);
                    System.exit(0);
                }

                if (player.isDead()) {
                    menu.setElements(Menu.Configuration.MULTIPLAYER_LOBBY);
                    menu.setVisible(true);
                    player.setDead(false);
                }

                view.repaint();
                Thread.sleep(Constants.DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
