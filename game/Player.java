package game;

import game.controller.Action;
import game.model.PlayerShip;

import java.io.Serializable;

/**
 * Player class that represents currently playing user.
 */
public class Player implements Serializable {
    private int id; //id of the player
    private String name; //nickname
    private transient Action action; //action taken currently
    private PlayerShip ship; //player's ship
    private int lives; //lives left
    private boolean dead; //determines if dead or not

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        lives = 5; //default lives number
    }

    public Player(int id, String name, PlayerShip ship) {
        this(id, name);
        this.ship = ship;
    }

    /**
     * Multiple of short methods like Getters/Setters. Controlling lives and the state of the player.
     */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Action getAction() {
        return action == null ? new Action() : action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public PlayerShip getShip() {
        return ship;
    }

    public void setShip(PlayerShip ship) {
        this.ship = ship;
    }

    public int getLives() {
        return lives;
    }

    public void decLives() {
        lives--;
    }

    public void resLives() {
        lives = 3;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
