package game.model;

import utilities.Vector2D;

import java.awt.*;

/**
 * Class representing bullet objects in the game. Inherits from GameObject abstract class.
 */
public class Bullet extends GameObject {
    private int time; //time to live
    private String source; //source of the bullet

    /**
     * Constructor. Calls constructor of superclass.
     *
     * @param position - position where the bullet should be created
     * @param velocity - velocity of the bullet
     * @param radius   - radius of the bullet
     * @param source   - source of the bullet
     */
    public Bullet(Vector2D position, Vector2D velocity, double radius, String source) {
        super(position, velocity, radius);
        time = 0;
        this.source = source;
    }

    /**
     * Adds time to the living bullet's time.
     */
    public void addTime() {
        if (++time >= 1000) this.dead = true;
    }

    /**
     * Draws the bullet (in this case as a green circles).
     *
     * @param g - graphics component
     */
    @Override
    public void draw(Graphics2D g) {
        switch (source) {
            case "Enemy":
                g.setColor(Color.RED);
                break;
            case "Player":
                g.setColor(Color.GREEN);
                break;
            case "Pod":
                g.setColor(Color.BLACK);
                break;
        }
        g.fillOval((int) (position.x - radius), (int) (position.y - radius), (int) (2 * radius), (int) (2 * radius));
    }

    /**
     * Method that makes sure the bullets do not collide with its source,
     * also prevents player getting shot by the Helper Pod, and enemies not shooting each other.
     *
     * @param other - reference to the other GameObject that collides
     * @return boolean whether or not the two objects can hit each other
     */
    @Override
    public boolean canHit(GameObject other) {
        if (this.source == "Player" && other instanceof PlayerShip) return false;
        else if (this.source == "Pod" && (other instanceof HelperPod || other instanceof PlayerShip)) return false;
        else if (this.source == "Enemy" && other instanceof EnemyShip) return false;
        return true;
    }
}
