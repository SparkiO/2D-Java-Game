package game.client;

import game.*;
import game.controller.Action;
import game.controller.Keys;
import game.controller.RLController;
import game.controller.WanderNShoot;
import game.model.*;
import game.GameWindow;
import utilities.SoundManager;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Local Client used in single player version of the game.
 */
public class LocalGameClient extends GameClient {

    public LocalGameClient(Difficulty difficulty, Leaderboard leaderboard) {
        super(difficulty, leaderboard);
    }

    /**
     * Initialises the game, view and key components required to play.
     * @param playerName nickname of the player to be added
     * @return true if the game has successfully started
     */
    @Override
    public boolean init(String playerName) {
        ScoreTracker scoreTracker = new ScoreTracker(difficulty.getRoundTime());
        game = new Game(scoreTracker, difficulty);
        game.init();
        player = game.newPlayer(playerName);

        keys = new Keys();
        keys.action().pause = true;
        view = new View(game);
        view.setPlayer(player);
        GameWindow gw = new GameWindow(view);
        gw.addKeyListener(keys);
        menu = new Menu(gw, keys.action());
        menu.setElements(Menu.Configuration.SINGLEPLAYER_LOBBY);
        menu.setVisible(true);

        new Thread(game).start();
        return true;
    }

    /**
     * Client loop handling actions stored in Action object and passing them to the Game. Calls View repaint.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Action action = keys.action();

                game.doAction(player.getId(), action);

                if (action.pause) {
                    game.pause();
                    action.pause = false;
                    menu.setVisible(true);
                }
                if (action.resume) {
                    game.resume();
                    action.resume = false;
                    menu.setVisible(false);
                }
                if (action.save && !player.getShip().getRespawning()) {
                    game.save();
                    action.save = false;
                }
                if (action.load) {
                    action.pause = false;
                    action.load = false;
                    game.stop();
                    try {
                        game = load();
                        view.setPlayer(player);
                        view.setGame(game);
                        new Thread(game).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (action.newgame) {
                    ScoreTracker scoreTracker = new ScoreTracker(difficulty.getRoundTime());
                    game = new Game(scoreTracker, difficulty);
                    game.init();
                    player = game.newPlayer(player.getName());
                    view.setGame(game);
                    view.setPlayer(player);
                    action.newgame = false;
                    action.pause = false;
                    new Thread(game).start();
                }
                if (action.exit) {
                    String playerName = player.getName();
                    int score = game.getScoreTracker().getScore(playerName);
                    if (score > 0)
                        leaderboard.saveScore(score, playerName);
                    System.exit(0);
                }

                if (player.isDead()) {
                    menu.setElements(Menu.Configuration.SINGLEPLAYER_LOBBY);
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

    /**
     * Loads a saved Game from a disk.
     * @return deserialised Game instance
     */
    public Game load() throws Exception {
        SoundManager.play(SoundManager.thrust);
        ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(Constants.SAVE_FILE));
        Game game = (Game) (in.readObject());
        in.close();

        PlayerShip playerShip = null;
        HelperPod p = null;
        for (GameObject o : game.objects) // find PlayerShip
            if (o instanceof Ship)
                if (o instanceof PlayerShip) {
                    playerShip = ((PlayerShip) o);
                    playerShip.setCtrl(keys);
                    player.setShip(playerShip);
                    break;
                }

        Set<RLController> rlControllers = new HashSet<>();
        for (GameObject o : game.objects) {
            o.load();

            if (o instanceof EnemyShip) { // restore EnemyShip controllers (requires PlayerShip reference)
                EnemyShip s = (EnemyShip) o;

                switch (s.getControllerName()) {
                    case ("game.controller.WanderNShoot"):
                        s.setCtrl(new WanderNShoot(o.getPosition(), player.getShip(), o.direction));
                        break;
                    default:
                        break;
                }
            } else if (o instanceof HelperPod) { // restore HelperPod controller (requires PlayerShip and Game reference)
                p = (HelperPod) o;
                RLController rlController = new RLController(game, playerShip);
                rlControllers.add(rlController);
                p.setCtrl(rlController);
                rlController.setPod(p);
            }
        }

        for (RLController rlc : rlControllers)
            rlc.init();

        game.pause = new Object();
        game.resume();
        game.getScoreTracker().restoreStartTime();
        return game;
    }
}
