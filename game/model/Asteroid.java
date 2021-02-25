package game.model;

import utilities.SoundManager;
import utilities.Sprite;
import utilities.Vector2D;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static game.Constants.*;

/**
 * Class representing Asteroids objects in the game. Inherits from GameObject abstract class.
 */
public class Asteroid extends GameObject {
    public static final double MAX_SPEED = 100; //max speed of the asteroid
    private transient Sprite asteroidSprite; //sprite used to represent the asteroid
    private Set<Asteroid> spawnedAsteroids; //list of newly spawned asteroids
    private String hitBy; //determines the source of collision

    /**
     * Constructor. Calls the constructor of superclass, assigns the sprite to the object.
     *
     * @param position - position of the Asteroid when spawned
     * @param velocity - velocity of the Asteroid
     * @param radius   - radius of the Asteroid
     */
    public Asteroid(Vector2D position, Vector2D velocity, int radius) {
        super(position, velocity, radius);
        asteroidSprite = new Sprite(Sprite.ASTEROID1, position, velocity, radius * 2, radius * 2);

    }

    public String getHitBy() {
        return hitBy;
    }

    /**
     * @return Set of newly spawned Asteroids objects.
     */
    public Set<Asteroid> getSpawnedAsteroids() {
        return spawnedAsteroids;
    }

    /**
     * Draws the sprite of the object.
     *
     * @param g - graphics component.
     */
    public void draw(Graphics2D g) {
        asteroidSprite.draw(g);
    }

    /**
     * Creates random Asteroid objects when called.
     *
     * @return Asteroid object
     */
    public static Asteroid makeRandomAsteroid() {
        int vx, vy;

        //decide if asteroid goes left or right
        if (Math.random() < 0.5)
            vx = (int) (Math.random() * MAX_SPEED);
        else
            vx = (int) (Math.random() * -MAX_SPEED);


        //decide if asteroid goes up or down
        if (Math.random() < 0.5)
            vy = (int) (Math.random() * MAX_SPEED);
        else
            vy = (int) (Math.random() * -MAX_SPEED);

        int x, y;
        x = (int) (Math.random() * WORLD_WIDTH);
        if (Math.random() < 0.5) {
            y = (int) (Math.random() * WORLD_HEIGHT / 3);
        } else {
            y = (int) (WORLD_HEIGHT - (Math.random() * WORLD_HEIGHT / 3));
        }
        Vector2D position = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(vx, vy);
        Asteroid asteroid = new Asteroid(position, velocity, 30);
        return asteroid;
    }

    /**
     * Checks which player hit the Asteroid - needed for multiplayer functionality.
     *
     * @param other - second Game Object to check
     */
    @Override
    public void collisionHandling(GameObject other) {
        super.collisionHandling(other);
        if (other instanceof PlayerShip)
            hitBy = ((PlayerShip) other).getName();
    }

    /**
     * @param other - reference to the other GameObject that collides
     * @return boolean whether or not the two objects can hit each other
     */
    @Override
    public boolean canHit(GameObject other) {
        if (other instanceof EnemyShip)
            return false;
        return super.canHit(other);
    }

    /**
     * Action taken when this object collides with other GameObject and gets hit.
     */
    @Override
    public void hit() {
        this.dead = true;

        //if radius is higher/equal than 15, create two smaller asteroids
        if (radius >= 15) {
            spawnedAsteroids = new HashSet<>();
            spawnedAsteroids.add(new Asteroid(new Vector2D(position.x + radius, position.y + radius), new Vector2D(-velocity.x, -velocity.y), (int) radius / 2));
            spawnedAsteroids.add(new Asteroid(new Vector2D(position), new Vector2D(velocity), (int) radius / 2));

            //play sounds depending on the asteroid's size when destroyed
            if (radius >= 30)
                SoundManager.play(SoundManager.bangLarge);
            else
                SoundManager.play(SoundManager.bangMedium);
        } else
            SoundManager.play(SoundManager.bangSmall);
    }

    /**
     * Required function from the instructions. It's called when two different Asteroids objects collide.
     * It repels them and changes their velocities; assumes that the two asteroids have the same mass.
     *
     * @param otherAsteroid - second Asteroid which collide with this object
     */
    public void asteroidInteract(Asteroid otherAsteroid) {
        Vector2D positionThis = new Vector2D(position);

        //finding minimum translation vector (so objects no longer collide)
        Vector2D difference = new Vector2D(positionThis.subtract(otherAsteroid.position));
        double distanceBetween = difference.mag();
        Vector2D minTranslationVec = new Vector2D(difference.mult(((radius + otherAsteroid.radius) - distanceBetween) / distanceBetween));

        //changing position of two asteroids so they no longer collide
        this.position.add(minTranslationVec);
        otherAsteroid.position.subtract(minTranslationVec);

        //to simplify, the asteroids have the same mass so v1 = v2, v2 = v1 after collision
        Vector2D v1 = new Vector2D(this.velocity);
        Vector2D v2 = new Vector2D(otherAsteroid.velocity);

        this.velocity.set(v2);
        otherAsteroid.velocity.set(v1);
    }

    /**
     * Load method used to load the sprite when user wants to load previously saved game state.
     */
    @Override
    public void load() {
        asteroidSprite = new Sprite(Sprite.ASTEROID1, position, velocity, radius * 2, radius * 2);
    }
}
