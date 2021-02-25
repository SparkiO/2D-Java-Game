package game.model;

import game.Constants;
import utilities.Vector2D;

import java.awt.*;

/**
 * Particle Objects created when other Game Objects get destroyed.
 */
public class Particle extends GameObject {
    public static final int PARTICLE_SPEED = 2; //speed of the particle
    public static final int TTL = 50; //maximum time to live
    public static final int SIZE = 3; //size of the particle
    public Color colour; //colour of the particle
    public int ttl; //time to live of this particle

    /**
     * @param position - position of the Game Object that got destroyed
     * @param velocity - velocity of the Game Object
     * @param type     -  type of the Game Object
     */
    public Particle(Vector2D position, Vector2D velocity, String type) {
        super(new Vector2D(position), randomVelocity().add(velocity), SIZE);
        this.ttl = Constants.RANDOM.nextInt(TTL); //generates random time to live

        //change colour of particles depending on the type of Game Object dead
        switch (type) {
            case "Asteroid":
                colour = Color.GRAY;
                break;
            case "EnemyShip":
                colour = Color.RED;
                break;
            case "Bullet":
                colour = Color.GREEN;
                break;
        }
    }

    /**
     * Generates random velocity for the particles.
     *
     * @return Vector2D with the velocity
     */
    public static Vector2D randomVelocity() {
        return Vector2D.polar(Math.random() * 2 * Math.PI,
                Math.abs(Constants.RANDOM.nextGaussian() * PARTICLE_SPEED));
    }

    /**
     * Updates the position and time to live.
     */
    @Override
    public void update() {
        this.position.add(velocity);
        --ttl;
    }

    /**
     * Draws the particles.
     *
     * @param g - graphic component
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(colour);
        g.fillOval((int) (position.x - SIZE), (int) (position.y - SIZE), 2 * SIZE, 2 * SIZE);

    }

    public int getTtl() {
        return ttl;
    }


    /**
     * Makes sure it doesn't collide with other Game Objects.
     *
     * @return always false boolean
     */
    @Override
    public boolean canHit(GameObject other) {
        return false;
    }
}
