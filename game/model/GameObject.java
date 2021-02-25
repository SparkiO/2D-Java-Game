package game.model;

import utilities.Vector2D;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import static game.Constants.*;

/**
 * Abstract GameObject class that represents all the Game Objects in the game.
 */
public abstract class GameObject implements Serializable {
    public Vector2D position; //position of the object
    public Vector2D velocity; //velocity of the object
    public Vector2D direction; //direction of the object
    public boolean dead; //dictates the state of the object
    public boolean invincible; //dictates if the object is in invincibility state
    public double radius; //radius to check collision handling

    /**
     * Constructor without direction vector (not needed for every GameObject)
     */
    public GameObject(Vector2D position, Vector2D velocity, double radius) {
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
    }

    /**
     * Constructor with direction vector
     */
    public GameObject(Vector2D position, Vector2D velocity, Vector2D direction, double radius) {
        this(position, velocity, radius);
        this.direction = direction;
    }

    /**
     * Checks whether two model overlap each other.
     *
     * @param other - second Game Object to check
     * @return boolean true if the objects collide, else false
     */
    public boolean overlap(GameObject other) {
        Area a1 = new Area(new Ellipse2D.Double(position.x, position.y, 2 * radius, 2 * radius));
        Area a2 = new Area(new Ellipse2D.Double(other.position.x, other.position.y, 2 * other.radius, 2 * other.radius));
        return a1.intersects(a2.getBounds2D());
    }

    /**
     * Calls the hit method if the two objects collide with each other.
     *
     * @param other - second Game Object to check
     */
    public void collisionHandling(GameObject other) {
        if (canHit(other) && this.getClass() != other.getClass() //makes sure you do not check two objects of the same class
                && this.overlap(other)) {
            this.hit();
        }
    }

    /**
     * Sets the object as dead.
     */
    public void hit() {
        this.dead = true;
    }

    /**
     * @param other - second Game Object to check
     * @return boolean true if the two object can hit with each other, else false
     */
    public boolean canHit(GameObject other) {
        return !invincible && !other.invincible && !(other instanceof BlackHole);
    }

    /**
     * Updates position and wraps it of the object.
     */
    public void update() {
        position.addScaled(velocity, DT);
        position.wrap(WORLD_WIDTH, WORLD_HEIGHT);
    }

    /**
     * Abstract draw method to be overriden by subclasses.
     */
    public abstract void draw(Graphics2D g);

    /**
     * Load method, to be overriden when needed.
     */
    public void load() {
    }

    /**
     * @return position vector of the object
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Sets the object invincible for given time, so it doesn't instantly collide again with another Game Object.
     *
     * @param invincible - boolean to make the object invincible or not invincible
     * @param time       - time to set the invincibility for
     */
    public void setInvincible(boolean invincible, long time) {
        this.invincible = invincible;
        Timer t = new Timer();
        t.schedule(new TimerTask() { //Schedules the Timer object to change the invincibility after given amount of time
            @Override
            public void run() {
                GameObject.this.invincible = !invincible;
            }
        }, time);
    }
}
