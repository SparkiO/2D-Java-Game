package game;

import game.controller.Action;
import game.controller.Controller;
import game.model.*;
import utilities.SoundManager;
import utilities.Vector2D;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The game class handles the game round. It keeps track of all the players, game objects and updates them.
 * Implements Serializable interface so it can be saved as well as sent over the network.
 * Implements Runnable interface so the main game loop can be run in a separate thread.
 */
public class Game implements Serializable, Runnable {
    private ScoreTracker scoreTracker; //tracks player scores
    public Set<GameObject> objects; //holds all alive game objects
    public Set<Particle> particles; //holds particles from a game object explosion
    public transient int numberOfPlayers = 0;
    public Player[] players; //array with players present in the game

    public transient Object pause; //serves as a lock for pause functionality
    private transient boolean paused; //indicates whether the game is paused
    private transient boolean isOn; //indicates whether game has not ended

    private transient final Difficulty difficulty; //holds current difficulty settings

    public Game(ScoreTracker scoreTracker, Difficulty difficulty) {
        this.scoreTracker = scoreTracker;
        this.difficulty = difficulty;
        players = new Player[Constants.MAX_PLAYER_NUMBER];

        particles = new HashSet<>();
        objects = new ConcurrentHashMap<>().newKeySet(); //thread-safe implementation of HashSet
        pause = new Object();

        isOn = true;
        paused = false;
        pause = new Object();
    }

    public Game(ScoreTracker scoreTracker) {
        this(scoreTracker, Constants.DEFAULT_DIFFICULTY);

    }

    /**
     * @return array with players playing this game round
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @return score tracker keeping track of all player scores
     */
    public ScoreTracker getScoreTracker() {
        return scoreTracker;
    }

    /**
     * Initialises the game.
     */
    public void init() {
        scoreTracker.setStartTime();

        for (int i = 0; i < difficulty.getNumerOfAsteroids(); i++) {
            Asteroid a = Asteroid.makeRandomAsteroid();
            objects.add(a);
        }

        List<BlackHole> otherHoles = new ArrayList<>();
        otherHoles.add(new BlackHole(new Vector2D(2000, 600), 100, otherHoles));
        otherHoles.add(new BlackHole(new Vector2D(400, 600), 100, otherHoles));
        otherHoles.add(new BlackHole(new Vector2D(900, 1100), 100, otherHoles));
        objects.addAll(otherHoles);
    }

    /**
     * Adds a new player to this game.
     * @param playerName nickname of the player to be added
     * @return Player object representing the new player added to the game
     */
    public synchronized Player newPlayer(String playerName) {
        if (numberOfPlayers >= Constants.MAX_PLAYER_NUMBER)
            return null;
        final int playerId = numberOfPlayers;
        numberOfPlayers++;

        Controller ctrl = () -> players[playerId].getAction();

        PlayerShip playerShip = new PlayerShip(ctrl, playerName, this);
        objects.add(playerShip);

        int numberOfEnemies = difficulty.getNumberOfEnemies();
        for (int i = 0; i < numberOfEnemies; i++) {
            EnemyShip es = EnemyShip.makeRandomEnemyShip(playerShip);
            objects.add(es);
        }
        scoreTracker.incEnemiesLeft(numberOfEnemies);

        players[playerId] = new Player(playerId, playerName, playerShip);

        return players[playerId];
    }

    /**
     * Makes an action for a specified player in the game.
     * @param playerId id of the player
     * @param action   Action performed by the player
     * @return updated Game instance
     */
    public synchronized Game doAction(int playerId, Action action) {
        players[playerId].setAction(action);
        return this;
    }

    /**
     * Main game loop to be executed in a thread.
     */
    @Override
    public void run() {
        isOn = true;
        while (isOn) {
            synchronized (pause) {
                update();
                try {
                    Thread.sleep(Constants.DELAY);
                    if (paused) {
                        scoreTracker.pausedTime();
                        pause.wait();
                        scoreTracker.restoreStartTime();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates all game objects.
     */
    private void update() {
        Set<GameObject> alive = new HashSet<>();

        scoreTracker.timer();
        if (scoreTracker.getTimeLeft() <= 0) { //if time left 0 - stops the game
            for (Player p : players)
                if (p != null)
                    p.setDead(true);
            isOn = false;
        }

        for (GameObject object : objects) {
            for (GameObject o2 : objects) { //for every pair of GameObjects checks Collision Handling
                o2.collisionHandling(object);

                //AsteroidInteract:
                if (o2 instanceof Asteroid && object instanceof Asteroid && o2 != object && o2.overlap(object)) {
                    ((Asteroid) o2).asteroidInteract((Asteroid) object);
                }
            }

            object.update();
            if (!object.dead) alive.add(object); //if not dead - add to list of alive
            else if (!(object instanceof Bullet)) explosion(object); //if dead - make an explostion

            if (object instanceof Bullet) { //handle time to live of Bullets
                ((Bullet) object).addTime();
            } else if (object instanceof Asteroid) { //handle Asteroids objects - scores and splitting
                Asteroid a = (Asteroid) object;
                if (a.dead) scoreTracker.incScore(ScoreTracker.ScoreType.ASTEROID, a.getHitBy());
                Set<Asteroid> s = a.getSpawnedAsteroids();
                if (s != null) {
                    alive.addAll(s);
                }
            }

            if (object instanceof Ship) {
                Ship s = (Ship) object;
                Bullet b = s.getBullet();
                if (b != null)
                    alive.add(b);

                if (s instanceof EnemyShip && s.dead) //reduce enemy number if is dead
                    scoreTracker.decEnemiesLeft();
            }
        }

        for (Player player : players) {
            if (player != null) {

                PlayerShip playerShip = player.getShip();
                HelperPod pod = playerShip.getPod();
                if (pod != null)
                    alive.add(pod);

                if (playerShip.isHit()) { //if player is hit - decrease life and make sound
                    player.decLives();
                    playerShip.setHit(false);
                    SoundManager.play(SoundManager.extraShip);
                }
                if (player.getLives() <= 0)
                    playerShip.dead = true;

                boolean respawning = playerShip.getRespawning();
                if (playerShip.dead) { //if player is dead - set respawning
                    if (!respawning) {
                        playerShip.setRespawning(true);
                        playerShip.setInvincible(true, 9000);
                    } else {
                        if (200 < playerShip.respawnTime) {
                            //recreates the ship
                            playerShip.setRespawning(false);
                            playerShip.dead = false;
                            playerShip.respawnTime = 0;
                            player.resLives();
                            alive.add(playerShip);
                        }
                    }
                }
                if (respawning)
                    playerShip.respawnTime++;

            }

            synchronized (Game.class) { //handle particles
                Iterator<Particle> it = particles.iterator();
                while (it.hasNext()) {
                    Particle particle = it.next();
                    if (particle.getTtl() > 0)
                        particle.update();
                    else
                        it.remove();
                }
            }

            objects.clear();
            objects.addAll(alive);
        }
    }

    /**
     * Explodes the object into particles.
     * @param object GameObject to be exploded
     */
    private void explosion(GameObject object) {
        synchronized (Game.class) {
            for (int i = 0; i < 100; i++) {
                Vector2D position = object.getPosition();
                String type = object.getClass().getName();
                particles.add(new Particle(position, new Vector2D(1, 1), type));
            }
        }
    }

    /**
     * Pauses the game.
     */
    public void pause() {
        paused = true; // variable is checked in the game loop to plac
    }

    /**
     * Resumes the game from a pause.
     */
    public void resume() {
        if (paused) {
            paused = false;
            synchronized (pause) {
                pause.notifyAll();
            }
        }
    }

    /**
     * Stops the game.
     */
    public void stop() {
        isOn = false;
    }

    /**
     * Serialises this game to a disk.
     */
    public void save() {
        try {
            scoreTracker.pausedTime();
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(Constants.SAVE_FILE));
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
